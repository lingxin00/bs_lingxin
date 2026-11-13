package org.example.bs_lingxin.member.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册入参
 *
 * 数据方向：前端 → 后端。接收前端提交的注册信息（手机号、密码、昵称），用于创建用户账号。
 */
@Data
public class UserRegisterDTO implements Serializable {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String nickname;
}
