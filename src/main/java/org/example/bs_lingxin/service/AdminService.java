package org.example.bs_lingxin.service;

import org.example.bs_lingxin.dto.AdminLoginDTO;
import org.example.bs_lingxin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.dto.AdminQueryDTO;
import org.example.bs_lingxin.dto.AdminSaveDTO;
import org.example.bs_lingxin.entity.Admin;

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
