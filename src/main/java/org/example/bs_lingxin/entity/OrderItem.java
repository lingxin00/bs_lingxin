package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * 订单明细实体
 * 对应数据库表 order_item
 * 作用：次订单内每一件购买SKU，评价、售后均关联本表，支持逻辑删除
 */
@Data
@TableName(value = "order_item", autoResultMap = true)
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long subOrderId;

    private Long skuId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> skuSnapshot;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
