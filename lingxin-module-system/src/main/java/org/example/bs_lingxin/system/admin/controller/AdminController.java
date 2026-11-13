package org.example.bs_lingxin.system.admin.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.annotation.OperationRecord;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.example.bs_lingxin.system.admin.dto.AdminLoginDTO;
import org.example.bs_lingxin.system.admin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminSaveDTO;
import org.example.bs_lingxin.system.admin.entity.Admin;
import org.example.bs_lingxin.system.admin.mapper.AdminMapper;
import org.example.bs_lingxin.system.admin.service.AdminService;
import org.example.bs_lingxin.system.admin.vo.AdminLoginVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
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
     * */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody AdminLoginDTO adminLoginDTO){
        Admin admin = adminService.login(adminLoginDTO);
        if (admin == null ){
            log.warn("管理员登录失败，输入用户名={}，原因：数据库查询为空",
                    adminLoginDTO.getUsername());
            return Result.fail(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        log.info("管理员登录成功，userId={},username={}",admin.getId(),admin.getUsername());
        StpUtil.login(admin.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        AdminLoginVO adminLoginVO = new AdminLoginVO(admin, tokenInfo);
        // Redis key：admin:online:token
        String redisKey = "admin:online:" + tokenInfo.getTokenValue();
        // JsonMapper（Jackson3）把对象转json字符串
        String json;
        try {
            json = jsonMapper.writeValueAsString(adminLoginVO);
        } catch (JacksonException e) {
            log.error("序列化在线管理员VO失败", e);
            return Result.fail(ResultCode.INTERNAL_SERVER_ERROR, "登录异常");
        }
        stringRedisTemplate.opsForValue().set(redisKey,json,tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
        return Result.success(new AdminLoginVO(admin,tokenInfo));
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
     * 分页查询管理员
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "分页查询管理员")
    @GetMapping("/page")
    public Result<?> selectPage(@Valid AdminPageQueryDTO adminPageQueryDTO){
        List<Admin> admins = adminService.selectPage(adminPageQueryDTO);
        Long total = adminMapper.selectAdminCount(adminPageQueryDTO);
        long pages = 0L;
        if(total != 0){
            pages = total % adminPageQueryDTO.getSize() == 0 ? total / adminPageQueryDTO.getSize() : total / adminPageQueryDTO.getSize() + 1;
        }
        if (admins == null){
            return Result.fail(ResultCode.INTERNAL_SERVER_ERROR, "查询管理员列表失败");
        }
        if (total == 0){
            return Result.fail(ResultCode.BAD_REQUEST, "暂无数据");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("records", admins);
        map.put("total", total);
        map.put("pages", pages);
        map.put("current", adminPageQueryDTO.getCurrent());
        map.put("size", adminPageQueryDTO.getSize());
        return Result.success(map);
    }

    /**
     * 查询单个管理员
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "查询单个管理员")
    @GetMapping("/{id}")
    public Result<?> selectId(Long id){
        if (id == null || id<=0){
            return Result.fail(ResultCode.BAD_REQUEST, "管理员ID不正确");
        }
        Admin admin = adminService.selectId(id);
        if (admin == null){
            return Result.fail(ResultCode.NOT_FOUND, "管理员不存在");
        }
        return Result.success(admin);
    }

    /**
     * 多条件查询单条管理员
     * */
    @OperationRecord(module = "管理员操作",type = "查询", desc = "多条件查询单个管理员")
    @GetMapping("/getOne")
    public Result<?> selectOne(@Valid AdminQueryDTO adminQueryDTO){
        if (adminQueryDTO == null){
            return Result.fail(ResultCode.BAD_REQUEST, "用户名为空");
        }
        Admin admin = adminService.selectOne(adminQueryDTO);
        if (admin == null){
            return Result.fail(ResultCode.NOT_FOUND, "管理员不存在");
        }
        return Result.success(admin);
    }

    /**
     * 新增管理员
     * */
    @OperationRecord(module = "管理员操作",type = "添加", desc = "添加管理员")
    @PostMapping("/add")
    public Result<?> add(@Valid AdminSaveDTO adminSaveDTO){
        if (adminSaveDTO  == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        adminService.addAdmin(adminSaveDTO);
        return Result.success();
    }

    /**
     *修改管理员
     * */
    @OperationRecord(module = "管理员操作",type = "修改", desc = "修改管理员")
    @PutMapping("/{id}")
    public Result<?> update(@Valid AdminSaveDTO adminSaveDTO){
        if (adminSaveDTO == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        adminService.updateAdmin(adminSaveDTO);
        return Result.success();
    }

    /**
     *逻辑删除管理员
     * */
    @OperationRecord(module = "管理员操作",type = "删除", desc = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        if (id == null){
            return Result.fail(ResultCode.BAD_REQUEST, "管理员ID不能为空");
        }
        adminService.removeLogicById(id);
        return Result.success();
    }

}
