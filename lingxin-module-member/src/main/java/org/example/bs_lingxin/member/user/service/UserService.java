package org.example.bs_lingxin.member.user.service;

import org.example.bs_lingxin.member.user.dto.UserLoginDTO;
import org.example.bs_lingxin.member.user.dto.UserPageQueryDTO;
import org.example.bs_lingxin.member.user.dto.UserProfileDTO;
import org.example.bs_lingxin.member.user.dto.UserRegisterDTO;
import org.example.bs_lingxin.member.user.dto.UserStatusDTO;
import org.example.bs_lingxin.member.user.dto.UserUpdateDTO;
import org.example.bs_lingxin.member.user.entity.User;
import org.example.bs_lingxin.member.user.vo.UserVO;

import java.util.List;

/**
 * 前端用户服务（C 端消费者）
 */
public interface UserService {

    /**
     * 手机号 + 密码登录，成功后刷新最后登录时间与 IP
     */
    User login(UserLoginDTO dto, String loginIp);

    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 后台分页查询用户
     */
    List<User> selectPage(UserPageQueryDTO dto);

    /**
     * 按ID查询用户（返回 VO，不含密码）
     */
    UserVO selectId(Long id);

    /**
     * 当前登录用户个人信息
     */
    UserVO info(Long userId);

    /**
     * 修改用户状态（1正常 0封禁）
     */
    void updateStatus(Long id, UserStatusDTO dto);

    /**
     * 用户自助修改资料（昵称、头像、邮箱）
     */
    void updateProfile(Long userId, UserProfileDTO dto);

    /**
     * 超管修改指定用户，id 来自路径变量，可改别人
     */
    void updateByAdmin(Long id, UserUpdateDTO dto);

    /**
     * 逻辑删除用户
     */
    void delete(Long id);
}
