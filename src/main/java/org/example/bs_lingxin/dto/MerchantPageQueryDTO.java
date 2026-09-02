package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MerchantPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String merchantName;
    private Integer status;
}
