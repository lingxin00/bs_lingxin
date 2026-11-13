package org.example.bs_lingxin.system.admin.service;

import org.example.bs_lingxin.system.admin.dto.AdminLoginDTO;
import org.example.bs_lingxin.system.admin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminSaveDTO;
import org.example.bs_lingxin.system.admin.entity.Admin;

import java.util.List;


public interface AdminService {

    Admin login(AdminLoginDTO adminLoginDTO);

    List<Admin> selectPage(AdminPageQueryDTO adminPageQueryDTO);

    Admin selectId(Long id);

    Admin selectOne(AdminQueryDTO adminQueryDTO);

    void addAdmin(AdminSaveDTO adminSaveDTO);

    void updateAdmin(AdminSaveDTO adminSaveDTO);

    void removeLogicById(Long id);

}
