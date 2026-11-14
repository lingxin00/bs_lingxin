package org.example.bs_lingxin.member.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 超管修改用户入参
 *
 * 数据方向：前端 → 后端。目标用户 id 走路径变量（PUT /api/user/{id}），不放这里。
 */
@Data
public class UserUpdateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //登录用户名，唯一
    private String username;
    //手机号，唯一，也是用户登录账号
    private String phone;
    //邮箱
    private String email;
    //头像 URL
    private String avatar;
    //昵称
    private String nickname;
    //状态：1正常 0封禁
    private Integer status;
    //新密码，为空表示不修改密码
    private String password;
}
