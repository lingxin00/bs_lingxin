package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * 主订单实体
 * 对应数据库表 main_order
 * 作用：用户一次下单总单，按商家拆分为多条次订单，支持逻辑删除
 */
@Data
@TableName(value = "main_order", autoResultMap = true)
public class MainOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> addressSnapshot;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer orderStatus;

    private Integer payStatus;

    private Long paymentId;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private LocalDateTime finishTime;

    private String remark;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
