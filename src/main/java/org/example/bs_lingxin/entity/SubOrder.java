package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 次订单实体
 * 对应数据库表 sub_order
 * 作用：一个主订单按商家拆分，商家独立发货物流载体，支持逻辑删除
 */
@Data
@TableName("sub_order")
public class SubOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String subOrderNo;

    private Long mainOrderId;

    private Long merchantId;

    private BigDecimal totalAmount;

    private Integer shippingStatus;

    private String logisticsNo;

    private String logisticsCompany;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
