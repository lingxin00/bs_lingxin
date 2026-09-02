package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RolePageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String roleName;
    private String roleCode;
    private Integer isDeleted;
}
