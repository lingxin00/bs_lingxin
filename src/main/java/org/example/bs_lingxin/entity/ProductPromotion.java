package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 活动SKU关联中间实体
 * 对应数据库表 product_promotion
 * 作用：绑定活动与参与商品SKU，设置活动价、限购，无逻辑删除，物理删除
 */
@Data
@TableName("product_promotion")
public class ProductPromotion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long skuId;

    private Long promotionId;

    private BigDecimal promotionPrice;

    private Integer limitQuantity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
