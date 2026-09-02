package org.example.bs_lingxin.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemVO implements Serializable {
    private Long id;
    private Long subOrderId;
    private Long skuId;
    private String skuSnapshot;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
