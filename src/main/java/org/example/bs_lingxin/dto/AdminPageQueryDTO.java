package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String username;
    private Integer status;
}
