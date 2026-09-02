package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

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
