package org.example.bs_lingxin.product.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * SKU 新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的规格、价格、库存与保质期信息。
 * specAttrs / images 直接接收 JSON 对象与数组，与实体 ProductSku 的字段类型保持一致。
 */
@Data
public class ProductSkuSaveDTO implements Serializable {
    private Long id;
    @NotNull(message = "关联商品id不能为空")
    private Long productId;
    //规格键值，如 {"重量":"500g"}
    private Map<String, Object> specAttrs;
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
    //SKU规格图片数组
    private List<String> images;
    private BigDecimal weight;
    private Integer status;
}
