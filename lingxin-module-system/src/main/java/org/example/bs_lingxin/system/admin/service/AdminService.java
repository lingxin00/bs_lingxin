package org.example.bs_lingxin.system.admin.service;

import org.example.bs_lingxin.system.admin.dto.AdminLoginDTO;
import org.example.bs_lingxin.system.admin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminProfileDTO;
import org.example.bs_lingxin.system.admin.dto.AdminQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminSaveDTO;
import org.example.bs_lingxin.system.admin.entity.Admin;
import org.example.bs_lingxin.system.admin.vo.AdminVO;

import java.util.List;


public interface AdminService {

    Admin login(AdminLoginDTO adminLoginDTO);

    List<Admin> selectPage(AdminPageQueryDTO adminPageQueryDTO);

    Admin selectId(Long id);

    Admin selectOne(AdminQueryDTO adminQueryDTO);

    void addAdmin(AdminSaveDTO adminSaveDTO);

    /**
     * 管理员修改自身资料，adminId 由 controller 从 token 解析后传入
     */
    void updateProfile(Long adminId, AdminProfileDTO adminProfileDTO);

    /**
     * 超级管理员修改指定管理员，adminId 来自路径变量，可改别人
     *
     * 相比 updateProfile 多放开 username / status / roleId 三个平台管控字段。
     */
    void updateAdmin(Long adminId, AdminSaveDTO adminSaveDTO);

    /**
     * 查询管理员详情，带 roleName，不含密码
     */
    AdminVO info(Long adminId);

    void removeLogicById(Long id);

}
