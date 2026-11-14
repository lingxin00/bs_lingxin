package org.example.bs_lingxin.member.user.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.constant.LoginDevice;
import org.example.bs_lingxin.member.user.dto.UserLoginDTO;
import org.example.bs_lingxin.member.user.dto.UserPageQueryDTO;
import org.example.bs_lingxin.member.user.dto.UserProfileDTO;
import org.example.bs_lingxin.member.user.dto.UserRegisterDTO;
import org.example.bs_lingxin.member.user.dto.UserStatusDTO;
import org.example.bs_lingxin.member.user.dto.UserUpdateDTO;
import org.example.bs_lingxin.member.user.entity.User;
import org.example.bs_lingxin.member.user.mapper.UserMapper;
import org.example.bs_lingxin.member.user.service.UserService;
import org.example.bs_lingxin.member.user.vo.UserLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 前端用户接口（C 端消费者）
 *
 * 登录流程与 MerchantController 一致：StpUtil.login → 取 token → 转 VO → 写 Redis 在线态。
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** Redis 在线用户 key 前缀，与 merchant:online 区分开 */
    private static final String ONLINE_KEY_PREFIX = "user:online:";
    /** last_login_ip 列是 VARCHAR(50)，超长会被数据库截断或报错 */
    private static final int MAX_IP_LENGTH = 50;

    private final UserService userService;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    public UserController(UserService userService, UserMapper userMapper, StringRedisTemplate stringRedisTemplate, JsonMapper jsonMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jsonMapper = jsonMapper;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginDTO dto, HttpServletRequest request) {
        User user = userService.login(dto, resolveIp(request));

        // 写入 device=user，拦截器据此拒绝用户 token 访问管理端/商家端接口
        StpUtil.login(user.getId(), new SaLoginModel().setDevice(LoginDevice.USER));
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        UserLoginVO vo = new UserLoginVO(user, tokenInfo);

        // 在线态缓存：以 token 为 key，TTL 与 token 有效期一致（秒）
        String redisKey = ONLINE_KEY_PREFIX + tokenInfo.getTokenValue();
        try {
            String json = jsonMapper.writeValueAsString(vo);
            stringRedisTemplate.opsForValue()
                    .set(redisKey, json, tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
        } catch (JacksonException e) {
            // 缓存写失败不影响登录，token 已经下发
            log.warn("在线用户缓存写入失败，userId={}", user.getId(), e);
        }

        log.info("用户登录成功，userId={},phone={}", user.getId(), user.getPhone());
        return Result.success(vo);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        log.info("用户注册成功，phone={}", dto.getPhone());
        return Result.success();
    }

    /**
     * 后台分页查询用户
     */
    @GetMapping("/page")
    public Result<?> page(@Valid UserPageQueryDTO dto) {
        List<User> records = userService.selectPage(dto);
        Long total = userMapper.selectUserCount(dto);
        return Result.success(buildPage(records, total, dto.getCurrent(), dto.getSize()));
    }

    /**
     * 当前登录用户个人信息
     */
    @GetMapping("/info")
    public Result<?> info() {
        return Result.success(userService.info(StpUtil.getLoginIdAsLong()));
    }

    /**
     * 超管：根据ID查询用户详情
     */
    @GetMapping("/{id:\\d+}")
    public Result<?> detail(@PathVariable("id") Long id) {
        return Result.success(userService.selectId(id));
    }

    /**
     * 修改用户状态（1正常 0封禁）
     */
    @PutMapping("/status/{id:\\d+}")
    public Result<?> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UserStatusDTO dto) {
        userService.updateStatus(id, dto);
        log.info("修改用户状态成功，id={}，status={}", id, dto.getStatus());
        return Result.success();
    }

    /**
     * 超管：按ID修改用户
     */
    @PutMapping("/{id:\\d+}")
    public Result<?> updateById(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDTO dto) {
        userService.updateByAdmin(id, dto);
        log.info("超管修改用户成功，id={}", id);
        return Result.success();
    }

    /**
     * 用户修改个人资料
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody UserProfileDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.updateProfile(userId, dto);
        log.info("用户修改个人资料成功，userId={}", userId);
        return Result.success();
    }

    /**
     * 超管：逻辑删除用户
     */
    @DeleteMapping("/{id:\\d+}")
    public Result<?> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        log.info("删除用户成功，id={}", id);
        return Result.success();
    }

    /**
     * 拼装分页返回结构，与 MerchantController.selectPage 保持一致
     */
    private Map<String, Object> buildPage(List<?> records, Long total, Long current, Long size) {
        long pages = 0L;
        if (total != null && total > 0) {
            pages = total % size == 0 ? total / size : total / size + 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        // 空结果也是正常业务，返回空列表 + total=0，不当作错误
        map.put("total", total == null ? 0L : total);
        map.put("pages", pages);
        map.put("current", current);
        map.put("size", size);
        return map;
    }

    /**
     * 取客户端真实 IP：优先 X-Forwarded-For 第一段，其次 X-Real-IP，最后 socket 地址
     */
    private String resolveIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能是 "客户端IP, 代理IP1, 代理IP2"，第一段才是真实客户端
            int comma = ip.indexOf(',');
            ip = comma > 0 ? ip.substring(0, comma) : ip;
        } else {
            ip = request.getHeader("X-Real-IP");
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        ip = ip == null ? null : ip.trim();
        if (ip != null && ip.length() > MAX_IP_LENGTH) {
            ip = ip.substring(0, MAX_IP_LENGTH);
        }
        return ip;
    }
}
