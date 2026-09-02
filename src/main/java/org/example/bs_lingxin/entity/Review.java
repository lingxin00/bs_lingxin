package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 商品评价实体
 * 对应数据库表 review
 * 作用：用户收货后评价，支持初评+多条追评，支持逻辑删除
 */
@Data
@TableName(value = "review", autoResultMap = true)
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderItemId;

    private Long parentReviewId;

    private Long userId;

    private Long skuId;

    private Integer reviewType;

    private Integer rating;

    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private Integer isAnonymous;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
