package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductSkuPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long productId;
    private Integer status;
    private Integer isPromotion;
}
