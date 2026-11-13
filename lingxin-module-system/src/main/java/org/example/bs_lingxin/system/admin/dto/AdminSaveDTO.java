package org.example.bs_lingxin.system.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的管理员表单；id 为空走新增，id 有值走修改。
 */
@Data
public class AdminSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


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
