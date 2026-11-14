package org.example.bs_lingxin.member.merchant.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家结算账户展示对象
 *
 * 数据方向：后端 → 前端。merchantName 为跨表字段，由 Service 查询时填充。
 */
@Data
public class SettlementAccountVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private Long merchantId;
    //跨表字段：商家名称
    private String merchantName;

    private String accountName;
    private String accountNumber;
    private String bankName;
    private Integer accountType; //1银行卡 2支付宝 3微信
    private Integer isDefault;   //1默认结算账户 0否
    private Integer status;      //1正常可用 0停用

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
