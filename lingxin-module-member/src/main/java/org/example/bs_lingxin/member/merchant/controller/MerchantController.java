package org.example.bs_lingxin.member.merchant.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.annotation.OperationRecord;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.example.bs_lingxin.member.merchant.dto.MerchantLoginDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantSaveDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;
import org.example.bs_lingxin.member.merchant.mapper.MerchantMapper;
import org.example.bs_lingxin.member.merchant.service.MerchantService;
import org.example.bs_lingxin.member.merchant.vo.MerchantLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
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
    public Result<?> login(@Valid @RequestBody MerchantLoginDTO merchantLoginDTO){
        Merchant merchant = merchantService.login(merchantLoginDTO);
        if (merchant == null){
            log.warn("商家登录失败，输入用户名={}，原因：数据库查询为空",
                    merchantLoginDTO.getUsername());
            return Result.fail(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        log.info("商家登录成功，userId={},username={}",merchant.getId(),merchant.getUsername());
        StpUtil.login(merchant.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        MerchantLoginVO merchantLoginVO = new MerchantLoginVO(merchant,tokenInfo);
        String redisKey = "merchant:online:" + tokenInfo.getTokenValue();
        String json;
        try {
            json = jsonMapper.writeValueAsString(merchantLoginVO);
        }catch (JacksonException e){
            log.error("序列化在线商家VO失败", e);
            return Result.fail(ResultCode.INTERNAL_SERVER_ERROR, "登录异常");
        }
        stringRedisTemplate.opsForValue().set(redisKey,json,tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
        return Result.success(new MerchantLoginVO(merchant,tokenInfo));
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
     * 分页查询商家
     * */
    @GetMapping("/page")
    public Result<?> selectPage(@Valid MerchantPageQueryDTO merchantPageQueryDTO){
        List<Merchant> merchants = merchantService.selectpage(merchantPageQueryDTO);
        Long total = merchantMapper.selectMerchantCount(merchantPageQueryDTO);
        long pages = 0L;
        if(total != 0){
            pages = total % merchantPageQueryDTO.getSize() == 0 ? total / merchantPageQueryDTO.getSize() : total / merchantPageQueryDTO.getSize() + 1;
        }
        if (merchants == null){
            return Result.fail(ResultCode.INTERNAL_SERVER_ERROR, "查询商家列表失败");
        }
        if (total == 0){
            return Result.fail(ResultCode.BAD_REQUEST, "暂无数据");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("records", merchants);
        map.put("total", total);
        map.put("pages", pages);
        map.put("current", merchantPageQueryDTO.getCurrent());
        map.put("size", merchantPageQueryDTO.getSize());
        return Result.success(map);
    }

    /**
     * 查询单个商家
     * */
    @GetMapping("/{id}")
    public Result<?> selectId(Long id){
        if (id == null || id<=0){
            return Result.fail(ResultCode.BAD_REQUEST, "商家ID不正确");
        }
        Merchant merchant = merchantService.selectId(id);
        if (merchant == null){
            return Result.fail(ResultCode.NOT_FOUND, "商家不存在");
        }
        return Result.success(merchant);
    }

    /**
     * 根据账号精确查询商家
     * */
    @GetMapping("/getByUsername")
    public Result<?> selectOne(@Valid MerchantPageQueryDTO merchantPageQueryDTO){
        if (merchantPageQueryDTO == null){
            return Result.fail(ResultCode.BAD_REQUEST, "用户名为空");
        }
        Merchant merchant = merchantService.selectName(merchantPageQueryDTO);
        if (merchant == null){
            return Result.fail(ResultCode.NOT_FOUND, "商家不存在");
        }
        return Result.success(merchant);
    }

    /**
     * 新增商家
     * */
    @PostMapping("/add")
    public Result<?> add(@Valid MerchantSaveDTO merchantSaveDTO){
        if (merchantSaveDTO  == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        merchantService.add(merchantSaveDTO);
        return Result.success();
    }

    /**
     *修改商家
     * */
    @PutMapping("/{id}")
    public Result<?> update(@Valid MerchantSaveDTO merchantSaveDTO){
        if (merchantSaveDTO == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        merchantService.update(merchantSaveDTO);
        return Result.success();
    }

    /**
     *逻辑删除商家
     * */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        if (id == null){
            return Result.fail(ResultCode.BAD_REQUEST, "商家ID不能为空");
        }
        merchantService.delect(id);
        return Result.success();
    }
}
