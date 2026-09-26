package org.example.bs_lingxin.service;

import org.example.bs_lingxin.dto.RolePageQueryDTO;
import org.example.bs_lingxin.dto.RoleSaveDTO;
import org.example.bs_lingxin.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> selectPage(RolePageQueryDTO rolePageQueryDTO);

    Role selectId(Long id);

    Role selectOne(RolePageQueryDTO rolePageQueryDTO);

    void addRole(RoleSaveDTO roleSaveDTO);

    void updateRole(RoleSaveDTO roleSaveDTO);

    void removeRole(Long id);

}
