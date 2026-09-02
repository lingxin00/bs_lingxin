package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
/**
 * 商品规格SKU实体
 * 对应数据库表 product_sku
 * 作用：食品规格、库存、价格、生产日期、临期预警核心表
 */
@Data
@TableName(value = "product_sku", autoResultMap = true)
public class ProductSku {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> specAttrs;

    private String skuCode;

    private BigDecimal price;

    private BigDecimal marketPrice;

    private BigDecimal costPrice;

    private Integer stock;

    private Integer soldCount;

    private LocalDate productionDate;

    private Integer expiryDays;

    private LocalDate expirationDate;

    private Integer warningThreshold;

    private Integer isPromotion;

    private BigDecimal promotionPrice;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private BigDecimal weight;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
