package org.example.bs_lingxin.service.impl;

import org.example.bs_lingxin.dto.RolePageQueryDTO;
import org.example.bs_lingxin.dto.RoleSaveDTO;
import org.example.bs_lingxin.entity.Role;
import org.example.bs_lingxin.exception.BusinessException;
import org.example.bs_lingxin.mapper.RoleMapper;
import org.example.bs_lingxin.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceimpl implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceimpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 分页查询角色
     */
    @Override
    public List<Role> selectPage(RolePageQueryDTO rolePageQueryDTO) {
        // 限制最大跳转页码20页
        final long MAX_ALLOW_PAGE = 50;
        Long current = rolePageQueryDTO.getCurrent();
        Long size = rolePageQueryDTO.getSize();
        if (current == null || current < 1){
            throw new BusinessException("页码不能小于1");
        }
        if (size == null || size < 1){
            throw new BusinessException("每页条数不能小于1");
        }
        if (current > MAX_ALLOW_PAGE){
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集");
        }
        Long offset = (current-1) * size;
        List<Role> roles = roleMapper.selectRolePageOffset(rolePageQueryDTO, offset);
        if (roles == null){
            throw new BusinessException("角色数据为空");
        }
        return roles;
    }

    /**
     * 根据ID查询角色
     * */
    @Override
    public Role selectId(Long id) {
        Role role = roleMapper.selectId(id);
        if (role == null){
            throw new BusinessException("账号不存在",400);
        }
        return role;
    }

    /**
     *精确查询单个角色
     * */
    @Override
    public Role selectOne(RolePageQueryDTO rolePageQueryDTO) {
        Role role = roleMapper.selectRoleOneByDto(rolePageQueryDTO);
        if (role == null){
            throw new BusinessException("角色数据为空");
        }
        return role;
    }

    /**
     * 新增角色
     * */
    @Override
    public void addRole(RoleSaveDTO roleSaveDTO) {
        int i = roleMapper.insertRole(roleSaveDTO);
        if (i<1){
            throw new BusinessException("新增角色失败，数据库未写入数据");
        }
    }

    /**
     * 修改角色
     * */
    @Override
    public void updateRole(RoleSaveDTO roleSaveDTO) {
        Role role = roleMapper.selectId(roleSaveDTO.getId());
        if (role == null){
            throw new BusinessException("当前角色不存在");
        }
        int i = roleMapper.updateRole(roleSaveDTO);
        if (i ==0){
            throw new BusinessException("修改角色失败，数据库未写入数据");
        }
    }

    /**
     *逻辑删除角色
     * */
    @Override
    public void removeRole(Long id) {
        Role role = roleMapper.selectId(id);
        if (role == null){
            throw new BusinessException("当前角色不存在");
        }
        int i = roleMapper.logicDeleteRole(id);
        if (i == 0){
            throw new RuntimeException("该角色已经被删除");
        }
    }
}
