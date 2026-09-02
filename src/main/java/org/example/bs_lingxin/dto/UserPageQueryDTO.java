package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String phone;
    private Integer status;
}
