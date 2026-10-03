package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RoleSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
    private String description;
    private Integer status;
    private Integer isDeleted;

    //可选时间范围筛选
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;

}
