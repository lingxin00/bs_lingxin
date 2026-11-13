package org.example.bs_lingxin.member.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 商家新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的商家资料与登录账号；id 为空走新增，id 有值走修改。
 */
@Data
public class MerchantSaveDTO implements Serializable {
    private Long id;
    @NotBlank(message = "登录账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String merchantName;
    private String merchantCode;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String logo;
    private String description;
    private Integer status;
}
