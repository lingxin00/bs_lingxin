package org.example.bs_lingxin.system.admin.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.annotation.OperationRecord;
import org.example.bs_lingxin.common.constant.LoginDevice;
import org.example.bs_lingxin.system.admin.dto.AdminLoginDTO;
import org.example.bs_lingxin.system.admin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminProfileDTO;
import org.example.bs_lingxin.system.admin.dto.AdminQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminSaveDTO;
import org.example.bs_lingxin.system.admin.entity.Admin;
import org.example.bs_lingxin.system.admin.mapper.AdminMapper;
import org.example.bs_lingxin.system.admin.service.AdminService;
import org.example.bs_lingxin.system.admin.vo.AdminLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
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

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    public AdminController(AdminService adminService,AdminMapper adminMapper,StringRedisTemplate stringRedisTemplate,JsonMapper jsonMapper){
        this.adminService = adminService;
        this.adminMapper=adminMapper;
        this.stringRedisTemplate=stringRedisTemplate;
        this.jsonMapper=jsonMapper;
    }

    /**
     * 管理员登录
     *
     * 登录时写入 device=admin，拦截器据此拒绝管理端 token 访问商家端/用户端接口
     * */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody AdminLoginDTO adminLoginDTO){
        Admin admin = adminService.login(adminLoginDTO);
        log.info("管理员登录成功，userId={},username={}",admin.getId(),admin.getUsername());
        StpUtil.login(admin.getId(), new SaLoginModel().setDevice(LoginDevice.ADMIN));
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        AdminLoginVO adminLoginVO = new AdminLoginVO(admin, tokenInfo);
        // Redis key：admin:online:token
        String redisKey = "admin:online:" + tokenInfo.getTokenValue();
        try {
            String json = jsonMapper.writeValueAsString(adminLoginVO);
            stringRedisTemplate.opsForValue().set(redisKey,json,tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
        } catch (JacksonException e) {
            // 缓存写失败不影响登录，token 已经下发
            log.warn("在线管理员缓存写入失败，adminId={}", admin.getId(), e);
        }
        return Result.success(adminLoginVO);
    }

    /**
     * 管理员退出
     * */
    @PostMapping("/logout")
    public Result<?> logout() {
        String token = StpUtil.getTokenValue();
        String redisKey = "admin:online:" + token;
        stringRedisTemplate.delete(redisKey);
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 查询当前登录管理员（id 从 token 解析）
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "查询当前登录管理员")
    @GetMapping("/info")
    public Result<?> info(){
        return Result.success(adminService.info(StpUtil.getLoginIdAsLong()));
    }

    /**
     * 管理员修改自身资料（id 从 token 解析，改不了别人）
     * */
    @OperationRecord(module = "管理员操作",type = "修改", desc = "修改个人资料")
    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody AdminProfileDTO adminProfileDTO){
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.updateProfile(adminId, adminProfileDTO);
        log.info("管理员修改个人资料成功，adminId={}", adminId);
        return Result.success();
    }

    /**
     * 超级管理员修改其他管理员（目标 id 走路径变量）
     *
     * 与 PUT /profile 的区别：那边只能改自己、且改不了 username/status/roleId；
     * 这里由管理端调用，可以指定任意管理员，账号、状态、角色都能改。
     * 路径加 \d+ 约束，避免和 /profile 这类字面量路径抢匹配。
     * */
    @OperationRecord(module = "管理员操作",type = "修改", desc = "修改其他管理员")
    @PutMapping("/{id:\\d+}")
    public Result<?> updateAdmin(@PathVariable("id") Long id,@Valid @RequestBody AdminSaveDTO adminSaveDTO){
        adminService.updateAdmin(id, adminSaveDTO);
        log.info("管理员修改其他管理员成功，operatorId={},targetId={}", StpUtil.getLoginIdAsLong(), id);
        return Result.success();
    }

    /**
     * 管理员注销自己的账号（逻辑删除，id 从 token 解析）
     * */
    @OperationRecord(module = "管理员操作",type = "删除", desc = "注销当前管理员账号")
    @DeleteMapping("/profile")
    public Result<?> deleteSelf(){
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.removeLogicById(adminId);
        stringRedisTemplate.delete("admin:online:" + StpUtil.getTokenValue());
        StpUtil.logout();
        log.info("管理员注销成功，adminId={}", adminId);
        return Result.success();
    }

    /**
     * 分页查询管理员
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "分页查询管理员")
    @GetMapping("/page")
    public Result<?> selectPage(@Valid AdminPageQueryDTO adminPageQueryDTO){
        List<Admin> admins = adminService.selectPage(adminPageQueryDTO);
        Long total = adminMapper.selectAdminCount(adminPageQueryDTO);
        long pages = 0L;
        if(total != null && total != 0){
            pages = total % adminPageQueryDTO.getSize() == 0 ? total / adminPageQueryDTO.getSize() : total / adminPageQueryDTO.getSize() + 1;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("records", admins);
        map.put("total", total == null ? 0L : total);
        map.put("pages", pages);
        map.put("current", adminPageQueryDTO.getCurrent());
        map.put("size", adminPageQueryDTO.getSize());
        return Result.success(map);
    }

    /**
     * 多条件查询单条管理员
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "多条件查询单个管理员")
    @GetMapping("/getOne")
    public Result<?> selectOne(@Valid AdminQueryDTO adminQueryDTO){
        Admin admin = adminService.selectOne(adminQueryDTO);
        return Result.success(admin);
    }

    /**
     * 新增管理员
     * */
    @OperationRecord(module = "管理员操作",type = "添加", desc = "添加管理员")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody AdminSaveDTO adminSaveDTO){
        adminService.addAdmin(adminSaveDTO);
        return Result.success();
    }

}
