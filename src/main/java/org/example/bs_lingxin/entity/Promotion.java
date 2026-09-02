package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * 促销活动实体
 * 对应数据库表 promotion
 * 作用：满减/折扣/秒杀/优惠券活动主表
 */
@Data
@TableName(value = "promotion", autoResultMap = true)
public class Promotion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String promotionName;

    private String promotionCode;

    private Integer promotionType;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> ruleConfig;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
