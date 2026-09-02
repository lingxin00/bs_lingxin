package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long categoryId;
    private Long merchantId;
    private String productName;
    private Integer status;
}
