package org.example.bs_lingxin.product.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * SKU 新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的规格、价格、库存与保质期信息。
 */
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
