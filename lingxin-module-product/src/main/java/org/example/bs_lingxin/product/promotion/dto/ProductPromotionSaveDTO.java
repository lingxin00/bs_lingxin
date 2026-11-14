package org.example.bs_lingxin.product.promotion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动SKU关联新增入参
 *
 * 数据方向：前端 → 后端。把某个SKU绑定到促销活动上，并设置专属活动价与限购数。
 */
@Data
public class ProductPromotionSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @NotNull(message = "活动ID不能为空")
    private Long promotionId;

    @NotNull(message = "活动价不能为空")
    @DecimalMin(value = "0", message = "活动价不能小于0")
    private BigDecimal promotionPrice;

    //单人限购数量，null表示不限购
    @Min(value = 1, message = "限购数量不能小于1")
    private Integer limitQuantity;
}
