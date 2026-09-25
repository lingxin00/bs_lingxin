package org.example.bs_lingxin.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.dto.AdminLoginDTO;
import org.example.bs_lingxin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.dto.AdminQueryDTO;
import org.example.bs_lingxin.dto.AdminSaveDTO;
import org.example.bs_lingxin.entity.Admin;
import org.example.bs_lingxin.mapper.AdminMapper;
import org.example.bs_lingxin.service.AdminService;
import org.example.bs_lingxin.vo.AdminLoginVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;

    public AdminController(AdminService adminService,AdminMapper adminMapper){
        this.adminService = adminService;
        this.adminMapper=adminMapper;
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
            return Result.fail();
        }
        log.info("管理员登录成功，userId={},username={}",admin.getId(),admin.getUsername());
        StpUtil.login(admin.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.success(new AdminLoginVO(admin,tokenInfo));
    }

    /**
     * 分页查询管理员
     * */
    @GetMapping("/page")
    public Result<?> selectPage(@Valid AdminPageQueryDTO adminPageQueryDTO){
        List<Admin> admins = adminService.selectPage(adminPageQueryDTO);
        Long total = adminMapper.selectAdminCount(adminPageQueryDTO);
        long pages = 0L;
        if(total != 0){
            pages = total % adminPageQueryDTO.getSize() == 0 ? total / adminPageQueryDTO.getSize() : total / adminPageQueryDTO.getSize() + 1;
        }
        if (admins == null){
            return Result.fail();
        }
        if (total == 0){
            return Result.fail(400,"暂无数据");
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
    @GetMapping("/{id}")
    public Result<?> selectId(Long id){
        if (id == null || id<=0){
            return Result.fail();
        }
        Admin admin = adminService.selectId(id);
        if (admin == null){
            return Result.fail();
        }
        return Result.success(admin);
    }

    /**
     * 多条件查询单条管理员
     * */
    @GetMapping("/getOne")
    public Result<?> selectOne(@Valid AdminQueryDTO adminQueryDTO){
        if (adminQueryDTO == null){
            return Result.fail(400,"用户名为空");
        }
        Admin admin = adminService.selectOne(adminQueryDTO);
        if (admin == null){
            return Result.fail();
        }
        return Result.success(admin);
    }

    /**
     * 新增管理员
     * */
    @PostMapping("/add")
    public Result<?> add(@Valid AdminSaveDTO adminSaveDTO){
        if (adminSaveDTO  == null){
            return Result.fail();
        }
        adminService.addAdmin(adminSaveDTO);
        return Result.success();
    }

    /**
     *修改管理员
     * */
    @PutMapping("/{id}")
    public Result<?> update(@Valid AdminSaveDTO adminSaveDTO){
        if (adminSaveDTO == null){
            return Result.fail();
        }
        adminService.updateAdmin(adminSaveDTO);
        return Result.success();
    }

    /**
     *逻辑删除管理员
     * */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        if (id == null){
            return Result.fail();
        }
        adminService.removeLogicById(id);
        return Result.success();
    }

}
