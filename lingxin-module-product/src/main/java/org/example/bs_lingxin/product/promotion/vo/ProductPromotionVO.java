package org.example.bs_lingxin.product.promotion.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 活动SKU关联展示对象
 *
 * 数据方向：后端 → 前端。除关联表本身的字段外，其余均为跨表展示字段，由 Service 查询时填充。
 */
@Data
public class ProductPromotionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private Long skuId;
    private Long promotionId;
    private BigDecimal promotionPrice;
    private Integer limitQuantity;

    //跨表字段：SKU与商品信息
    private Long productId;
    private String productName;
    private String skuCode;
    private Map<String, Object> specAttrs;
    //SKU原售价，便于前端与活动价对比展示
    private BigDecimal originalPrice;

    //跨表字段：活动名称
    private String promotionName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
