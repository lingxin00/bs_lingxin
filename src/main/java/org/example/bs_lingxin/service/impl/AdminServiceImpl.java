package org.example.bs_lingxin.service.impl;

import org.example.bs_lingxin.dto.AdminLoginDTO;
import org.example.bs_lingxin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.dto.AdminQueryDTO;
import org.example.bs_lingxin.dto.AdminSaveDTO;
import org.example.bs_lingxin.entity.Admin;
import org.example.bs_lingxin.exception.BusinessException;
import org.example.bs_lingxin.mapper.AdminMapper;
import org.example.bs_lingxin.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminMapper adminMapper,PasswordEncoder passwordEncoder) {
        this.adminMapper=adminMapper;
        this.passwordEncoder=passwordEncoder;
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
            throw new BusinessException("账号不存在",400);
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
            throw new BusinessException("账号不存在",400);
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
        if (i<1){
            throw new BusinessException("新增管理员失败，数据库未写入数据");
        }
    }

    /**
     * 修改管理员
     * */
    @Override
    public void updateAdmin(AdminSaveDTO adminSaveDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminSaveDTO, admin);      //复制adminSaveDTO到admin
        // 修改：密码不为空才加密更新；null/空串不修改密码
        if(adminSaveDTO.getPassword() != null && !adminSaveDTO.getPassword().isBlank()){    //isBlank 判断字符串是不是：空字符串 "" 或者全是空格 "  "
            String encodePwd = passwordEncoder.encode(adminSaveDTO.getPassword());
            admin.setPassword(encodePwd);
        }else{
            admin.setPassword(null);
            // xml updateAdmin里面 <if test="password != null"> 就会跳过password字段更新，保留旧密码
        }
        int i = adminMapper.updateAdmin(admin);
        if (i ==0){
            throw new BusinessException("修改管理员失败，数据库未写入数据");
        }
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
