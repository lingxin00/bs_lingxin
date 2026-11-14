package org.example.bs_lingxin.member.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 商家新增 / 超管修改入参
 *
 * 数据方向：前端 → 后端。
 */
@Data
public class MerchantSaveDTO implements Serializable {
    private Long id;
    @NotBlank(message = "登录账号不能为空")
    private String username;
    /**
     * 新增：必填；超管修改：可以不传，不传代表不修改密码
     */
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
