package org.example.bs_lingxin.system.admin.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员修改自身资料入参
 *
 * 数据方向：前端 → 后端。管理员登录后只能改自己，id 由 token 解析，不在入参中传递。
 * 不含 username / status / roleId：账号、启停用、角色属于超管管控，管理员自己改不了。
 */
@Data
public class AdminProfileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    //新密码，为空表示不修改密码
    private String password;
}
