package org.example.bs_lingxin.member.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 商家登录入参
 *
 * 数据方向：前端 → 后端。接收前端提交的商家账号和密码，仅用于登录校验。
 */
@Data
public class MerchantLoginDTO implements Serializable {
    @NotBlank(message = "账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
