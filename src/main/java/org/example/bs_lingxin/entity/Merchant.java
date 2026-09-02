package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 商家入驻实体
 * 对应数据库表 merchant
 * 作用：入驻食品店铺主体，商家独立登录后台管理商品订单
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String merchantName;

    private String merchantCode;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String logo;

    private String description;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
