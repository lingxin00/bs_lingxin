package org.example.bs_lingxin.system.admin.service.impl;

import org.example.bs_lingxin.system.admin.dto.AdminLoginDTO;
import org.example.bs_lingxin.system.admin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminProfileDTO;
import org.example.bs_lingxin.system.admin.dto.AdminQueryDTO;
import org.example.bs_lingxin.system.admin.dto.AdminSaveDTO;
import org.example.bs_lingxin.system.admin.entity.Admin;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.system.admin.mapper.AdminMapper;
import org.example.bs_lingxin.system.admin.service.AdminService;
import org.example.bs_lingxin.system.admin.vo.AdminVO;
import org.example.bs_lingxin.system.role.entity.Role;
import org.example.bs_lingxin.system.role.mapper.RoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    public AdminServiceImpl(AdminMapper adminMapper,PasswordEncoder passwordEncoder,RoleMapper roleMapper) {
        this.adminMapper=adminMapper;
        this.passwordEncoder=passwordEncoder;
        this.roleMapper=roleMapper;
    }

    /**
     * 登录，校验解密密码
     * */
    @Override
    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username=adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null){
            throw new BusinessException("账号不存在",401);
        }
        boolean ok=passwordEncoder.matches(password,admin.getPassword());   //解密 matches(前端明文,数据库密文)
        if (ok){
            return adminMapper.selectByUsername(username);
        }
        throw new BusinessException("密码不匹配",400);
    }

    /**
     * 分页查询管理员
     */
    @Override
    public List<Admin> selectPage(AdminPageQueryDTO adminPageQueryDTO) {
        // 限制最大跳转页码20页
        final long MAX_ALLOW_PAGE = 50;
        Long current = adminPageQueryDTO.getCurrent();
        Long size = adminPageQueryDTO.getSize();
        if (current == null || current < 1) {
            throw new BusinessException("页码不能小于1");
        }
        if (size == null || size < 1) {
            throw new BusinessException("每页条数不能小于1");
        }
        if (current > MAX_ALLOW_PAGE) {
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集");
        }
        Long offset = (current-1) * size;
        List<Admin> admins= adminMapper.selectAdminPageOffset(adminPageQueryDTO,offset);
        if (admins == null){
            throw new BusinessException("管理员数据为空");
        }
        return admins;
    }

    /**
     * 根据ID查询管理员
     * */
    @Override
    public Admin selectId(Long id) {
        Admin admin = adminMapper.selectId(id);
        if (admin == null){
            throw new BusinessException("账号不存在",401);
        }
        return admin;
    }

    /**
     *根据用户名精确查询管理员
     * */
    @Override
    public Admin selectOne(AdminQueryDTO adminQueryDTO) {
        Admin admin = adminMapper.selectAdminOneByDto(adminQueryDTO);
        if (admin == null){
            throw new BusinessException("管理员数据为空");
        }
        return admin;
    }

    /**
     * 新增管理员
     * */
    @Override
    public void addAdmin(AdminSaveDTO adminSaveDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminSaveDTO,admin);
        String encodePwd = passwordEncoder.encode(adminSaveDTO.getPassword());
        admin.setPassword(encodePwd);
        int i = adminMapper.insertAdmin(admin);
        if (i < 1){
            throw new BusinessException("新增管理员失败，数据库未写入数据");
        }
    }

    /**
     * 管理员修改自身资料
     *
     * adminId 由 controller 从 token 解析，前端传不了别人的 id。
     * 只允许改 realName / phone / email / avatar / password，
     * username、status、roleId 不在 DTO 里，不会被覆盖。
     * */
    @Override
    public void updateProfile(Long adminId, AdminProfileDTO adminProfileDTO) {
        Admin admin1 = adminMapper.selectId(adminId);
        if (admin1 == null){
            throw new BusinessException("管理员不存在", 404);
        }
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setRealName(adminProfileDTO.getRealName());
        admin.setPhone(adminProfileDTO.getPhone());
        admin.setEmail(adminProfileDTO.getEmail());
        admin.setAvatar(adminProfileDTO.getAvatar());
        // 修改：密码不为空才加密更新；null/空串不修改密码
        if (StringUtils.hasText(adminProfileDTO.getPassword())){
            admin.setPassword(passwordEncoder.encode(adminProfileDTO.getPassword()));
        }
        // xml updateAdmin 里用 <if test="xxx != null"> 判断，null 字段不会被拼进 SET
        int i = adminMapper.updateAdmin(admin);
        if (i ==0){
            throw new BusinessException("修改个人资料失败，数据库未写入数据");
        }
    }

    /**
     * 超级管理员修改指定管理员
     *
     * 与 updateProfile 的区别：目标 id 来自路径，可以改别人，
     * 并且放开 username / status / roleId 三个平台管控字段。
     * */
    @Override
    public void updateAdmin(Long adminId, AdminSaveDTO adminSaveDTO) {
        Admin exist = adminMapper.selectId(adminId);
        if (exist == null) {
            throw new BusinessException("管理员不存在", 404);
        }
        // username 是唯一索引，改成别人正占用的值会抛 DuplicateKeyException 变成 500，先查一次给可读提示
        String newUsername = adminSaveDTO.getUsername();
        if (StringUtils.hasText(newUsername)
                && !newUsername.equals(exist.getUsername())
                && adminMapper.countUsernameExcludeId(newUsername, adminId) > 0) {
            throw new BusinessException("账号已被占用：" + newUsername, 400);
        }
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setUsername(newUsername);
        admin.setRealName(adminSaveDTO.getRealName());
        admin.setPhone(adminSaveDTO.getPhone());
        admin.setEmail(adminSaveDTO.getEmail());
        admin.setAvatar(adminSaveDTO.getAvatar());
        admin.setStatus(adminSaveDTO.getStatus());
        admin.setRoleId(adminSaveDTO.getRoleId());
        // 密码有值才加密更新；null/空串不传，xml 里 password != null 才拼进 SET，保留原密码
        if (StringUtils.hasText(adminSaveDTO.getPassword())) {
            admin.setPassword(passwordEncoder.encode(adminSaveDTO.getPassword()));
        }
        if (adminMapper.updateAdmin(admin) == 0) {
            throw new BusinessException("修改管理员失败，数据库未写入数据");
        }
    }

    /**
     * 查询管理员详情，密码不外泄，roleName 额外查角色表补上
     * */
    @Override
    public AdminVO info(Long adminId) {
        Admin admin = adminMapper.selectId(adminId);
        if (admin == null){
            throw new BusinessException("管理员不存在", 404);
        }
        AdminVO adminVO = new AdminVO(admin);
        if (admin.getRoleId() != null){
            Role role = roleMapper.selectId(admin.getRoleId());
            if (role != null){
                adminVO.setRoleName(role.getRoleName());
            }
        }
        return adminVO;
    }

    /**
     *逻辑删除管理员
     * */
    @Override
    public void removeLogicById(Long id) {
        // 先判断这条记录是否存在
        Admin admin = adminMapper.selectId(id);
        if(admin == null){
            throw new RuntimeException("该管理员不存在");
        }
        int rows = adminMapper.logicDeleteAdmin(id);
        // rows=0 代表本来已经是 is_deleted=1，已经删除过
        if(rows == 0){
            throw new RuntimeException("该管理员已经被删除");
        }
    }
}
