package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id：新增不传；修改必须传
     */
    private Long id;

    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * 新增：必填；修改：可以不传，不传代表不修改密码
     */
    private String password;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer status;

    private Long roleId;
}
