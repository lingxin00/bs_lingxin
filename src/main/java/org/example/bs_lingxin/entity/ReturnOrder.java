package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 售后退款退货实体
 * 对应数据库表 return_order
 * 作用：售后申请单，支持仅退款、退货退款，支持逻辑删除
 */
@Data
@TableName(value = "return_order", autoResultMap = true)
public class ReturnOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String returnNo;

    private Long orderItemId;

    private Long userId;

    private Integer returnType;

    private String reason;

    private String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private BigDecimal returnAmount;

    private Integer status;

    private String auditRemark;

    private Long auditorId;

    private LocalDateTime auditTime;

    private LocalDateTime refundTime;

    private LocalDateTime receiveGoodsTime;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
