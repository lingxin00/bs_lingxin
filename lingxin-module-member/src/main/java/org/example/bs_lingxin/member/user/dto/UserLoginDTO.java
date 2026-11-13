package org.example.bs_lingxin.member.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录入参
 *
 * 数据方向：前端 → 后端。接收前端提交的手机号和密码，仅用于登录校验。
 */
@Data
public class UserLoginDTO implements Serializable {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;
}
