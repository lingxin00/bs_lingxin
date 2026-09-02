package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Integer orderStatus;
}
