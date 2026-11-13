package org.example.bs_lingxin.system.role.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.example.bs_lingxin.system.role.dto.RolePageQueryDTO;
import org.example.bs_lingxin.system.role.dto.RoleSaveDTO;
import org.example.bs_lingxin.system.role.entity.Role;
import org.example.bs_lingxin.system.role.mapper.RoleMapper;
import org.example.bs_lingxin.system.role.service.RoleService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    /**
     * 分页查询角色
     * */
    @GetMapping("/page")
    public Result<?> selectPage(@Valid RolePageQueryDTO rolePageQueryDTO){
        List<Role> roles = roleService.selectPage(rolePageQueryDTO);
        Long total = roleMapper.selectRoleCount(rolePageQueryDTO);
        long pages = 0L;
        if (total != 0){
            pages = total % rolePageQueryDTO.getSize() == 0 ? total / rolePageQueryDTO.getSize() : total / rolePageQueryDTO.getSize() + 1;
        }
        if (roles == null){
            return Result.fail(ResultCode.INTERNAL_SERVER_ERROR, "查询角色列表失败");
        }
        if (total == 0){
            return Result.fail(ResultCode.BAD_REQUEST, "暂无数据");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("records", roles);
        map.put("total", total);
        map.put("pages", pages);
        map.put("current", rolePageQueryDTO.getCurrent());
        map.put("size", rolePageQueryDTO.getSize());
        return Result.success(map);
    }

    /**
     * 查询单个角色
     * */
    @GetMapping("/{id}")
    public Result<?> selectId(Long id){
        if (id == null || id<=0){
            return Result.fail(ResultCode.BAD_REQUEST, "角色ID不正确");
        }
        Role role = roleService.selectId(id);
        if (role == null){
            return Result.fail(ResultCode.NOT_FOUND, "角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 多条件查询单条角色
     * */
    @GetMapping("/getOne")
    public Result<?> selectOne(@Valid RolePageQueryDTO rolePageQueryDTO){
        if (rolePageQueryDTO == null){
            return Result.fail(ResultCode.BAD_REQUEST, "角色为空");
        }
        Role role = roleService.selectOne(rolePageQueryDTO);
        if (role == null){
            return Result.fail(ResultCode.NOT_FOUND, "角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 新增角色
     * */
    @PostMapping("/add")
    public Result<?> add(@Valid RoleSaveDTO roleSaveDTO){
        if (roleSaveDTO  == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        roleService.addRole(roleSaveDTO);
        return Result.success();
    }

    /**
     *修改角色
     * */
    @PutMapping("/{id}")
    public Result<?> update(Role role){
        if (role == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        roleService.updateRole(role);
        return Result.success();
    }

    /**
     *逻辑删除角色
     * */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        if (id == null){
            return Result.fail(ResultCode.BAD_REQUEST, "角色ID不能为空");
        }
        roleService.removeRole(id);
        return Result.success();
    }


}
