package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * 支付流水实体
 * 对应数据库表 payment
 * 作用：所有支付、退款财务记录，禁止物理删除，支持逻辑删除
 */
@Data
@TableName(value = "payment", autoResultMap = true)
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentNo;

    private String orderNo;

    private Long userId;

    private BigDecimal payAmount;

    private String payMethod;

    private Integer payStatus;

    private String thirdPartyOrder;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> callbackData;

    private LocalDateTime payTime;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
