package org.example.bs_lingxin.member.merchant.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.constant.LoginDevice;
import org.example.bs_lingxin.member.merchant.dto.MerchantLoginDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantProfileDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantSaveDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;
import org.example.bs_lingxin.member.merchant.mapper.MerchantMapper;
import org.example.bs_lingxin.member.merchant.service.MerchantService;
import org.example.bs_lingxin.member.merchant.vo.MerchantLoginVO;
import org.example.bs_lingxin.member.merchant.vo.MerchantVO;
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
@RequestMapping("/api/merchant")
public class MerchantController {
    private final MerchantService merchantService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;
    private final MerchantMapper merchantMapper;

    public MerchantController(MerchantService merchantService, StringRedisTemplate stringRedisTemplate, JsonMapper jsonMapper, MerchantMapper merchantMapper) {
        this.merchantService = merchantService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jsonMapper = jsonMapper;
        this.merchantMapper = merchantMapper;
    }

    /**
     * 商家登录
     * */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody MerchantLoginDTO merchantLoginDTO){
        Merchant merchant = merchantService.login(merchantLoginDTO);
        log.info("商家登录成功，userId={},username={}",merchant.getId(),merchant.getUsername());
        StpUtil.login(merchant.getId(), new SaLoginModel().setDevice(LoginDevice.MERCHANT));
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        MerchantLoginVO merchantLoginVO = new MerchantLoginVO(merchant,tokenInfo);
        String redisKey = "merchant:online:" + tokenInfo.getTokenValue();
        try {
            String json = jsonMapper.writeValueAsString(merchantLoginVO);
            stringRedisTemplate.opsForValue().set(redisKey,json,tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
        }catch (JacksonException e){
            // 缓存写失败不影响登录，token 已经下发
            log.warn("在线商家缓存写入失败，merchantId={}", merchant.getId(), e);
        }
        return Result.success(merchantLoginVO);
    }

    /**
     * 商家退出
     * */
    @PostMapping("/logout")
    public Result<?> logout() {
        String token = StpUtil.getTokenValue();
        String redisKey = "merchant:online:" + token;
        stringRedisTemplate.delete(redisKey);
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 查询当前登录商家（id 从 token 解析）
     * */
    @GetMapping("/info")
    public Result<?> info(){
        Long merchantId = StpUtil.getLoginIdAsLong();
        // 必须转 VO：实体带 password 哈希，不能直接返回
        return Result.success(new MerchantVO(merchantService.selectId(merchantId)));
    }

    /**
     * 商家修改自身资料（id 从 token 解析，改不了别人）
     * */
    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody MerchantProfileDTO merchantProfileDTO){
        Long merchantId = StpUtil.getLoginIdAsLong();
        merchantService.updateProfile(merchantId, merchantProfileDTO);
        log.info("商家修改资料成功，merchantId={}", merchantId);
        return Result.success();
    }

    /**
     * 商家注销自己的账号（逻辑删除，id 从 token 解析）
     * */
    @DeleteMapping("/profile")
    public Result<?> deleteSelf(){
        Long merchantId = StpUtil.getLoginIdAsLong();
        merchantService.delect(merchantId);
        stringRedisTemplate.delete("merchant:online:" + StpUtil.getTokenValue());
        StpUtil.logout();
        log.info("商家注销成功，merchantId={}", merchantId);
        return Result.success();
    }

    /**
     * 分页查询商家
     * */
    @GetMapping("/page")
    public Result<?> selectPage(@Valid MerchantPageQueryDTO merchantPageQueryDTO){
        List<Merchant> merchants = merchantService.selectpage(merchantPageQueryDTO);
        Long total = merchantMapper.selectMerchantCount(merchantPageQueryDTO);
        long pages = 0L;
        if(total != null && total != 0){
            pages = total % merchantPageQueryDTO.getSize() == 0 ? total / merchantPageQueryDTO.getSize() : total / merchantPageQueryDTO.getSize() + 1;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("records", merchants);
        map.put("total", total == null ? 0L : total);
        map.put("pages", pages);
        map.put("current", merchantPageQueryDTO.getCurrent());
        map.put("size", merchantPageQueryDTO.getSize());
        return Result.success(map);
    }

    /**
     * 根据账号精确查询商家
     * */
    @GetMapping("/getByUsername")
    public Result<?> selectOne(@Valid MerchantPageQueryDTO merchantPageQueryDTO){
        Merchant merchant = merchantService.selectName(merchantPageQueryDTO);
        return Result.success(new MerchantVO(merchant));
    }

    /**
     * 新增商家
     * */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody MerchantSaveDTO merchantSaveDTO){
        merchantService.add(merchantSaveDTO);
        return Result.success();
    }

    /**
     * 超管：按ID查询商家详情
     * */
    @GetMapping("/{id:\\d+}")
    public Result<?> selectById(@PathVariable("id") Long id){
        return Result.success(new MerchantVO(merchantService.selectId(id)));
    }

    /**
     * 超管：按ID修改商家
     * */
    @PutMapping("/{id:\\d+}")
    public Result<?> updateById(@PathVariable("id") Long id,@Valid @RequestBody MerchantSaveDTO merchantSaveDTO){
        merchantService.updateAdmin(id, merchantSaveDTO);
        log.info("超管修改商家成功，merchantId={}", id);
        return Result.success();
    }

    /**
     * 超管：按ID逻辑删除商家
     * */
    @DeleteMapping("/{id:\\d+}")
    public Result<?> deleteById(@PathVariable("id") Long id){
        merchantService.delect(id);
        log.info("超管删除商家成功，merchantId={}", id);
        return Result.success();
    }
}
