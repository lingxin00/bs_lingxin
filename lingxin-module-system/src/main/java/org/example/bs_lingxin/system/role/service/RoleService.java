package org.example.bs_lingxin.system.role.service;

import org.example.bs_lingxin.system.role.dto.RolePageQueryDTO;
import org.example.bs_lingxin.system.role.dto.RoleSaveDTO;
import org.example.bs_lingxin.system.role.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> selectPage(RolePageQueryDTO rolePageQueryDTO);

    Role selectId(Long id);

    Role selectOne(RolePageQueryDTO rolePageQueryDTO);

    void addRole(RoleSaveDTO roleSaveDTO);

    void updateRole(Role role1);

    void removeRole(Long id);

}
