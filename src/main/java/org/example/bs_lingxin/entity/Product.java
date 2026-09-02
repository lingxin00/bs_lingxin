package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 商品SPU主实体
 * 对应数据库表 product
 * 作用：商品公共基础信息，一个商品对应多条SKU规格
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productName;

    private String productCode;

    private Long categoryId;

    private Long merchantId;

    private String brand;

    private String description;

    private String mainImage;

    private String detailHtml;

    private Integer status;

    private Integer sortOrder;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
