package org.example.bs_lingxin.member.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册入参
 *
 * 数据方向：前端 → 后端。接收前端提交的注册信息（用户名、手机号、密码、昵称），用于创建用户账号。
 */
@Data
public class UserRegisterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //用户名，表上是 NOT NULL + 唯一索引，必须由前端传入
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;
}
