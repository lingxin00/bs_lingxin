package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 商家结算账户实体
 * 对应数据库表 settlement_account
 * 作用：商家收款银行卡/支付宝/微信，平台结算打款使用，无逻辑删除，物理删除
 */
@Data
@TableName("settlement_account")
public class SettlementAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private String accountName;

    private String accountNumber;

    private String bankName;

    private Integer accountType;

    private Integer isDefault;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
