package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductSkuSaveDTO implements Serializable {
    private Long id;
    @NotNull(message = "关联商品id不能为空")
    private Long productId;
    private String specAttrs;
    private String skuCode;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private LocalDate productionDate;
    private Integer expiryDays;
    private LocalDate expirationDate;
    private Integer warningThreshold;
    private Integer isPromotion;
    private BigDecimal promotionPrice;
    private String images;
    private BigDecimal weight;
    private Integer status;
}
