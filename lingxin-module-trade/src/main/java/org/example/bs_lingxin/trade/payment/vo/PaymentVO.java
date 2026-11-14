package org.example.bs_lingxin.trade.payment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水展示对象
 *
 * 数据方向：后端 → 前端。callbackData（第三方回调原始报文）属于对账内部数据，不对外返回。
 */
@Data
public class PaymentVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private String paymentNo;
    private String orderNo;
    private Long userId;
    private BigDecimal payAmount;
    private String payMethod;      //WECHAT / ALIPAY / BALANCE
    private Integer payStatus;     //0待付 1成功 2失败 3已退款
    private String thirdPartyOrder; //微信/支付宝外部单号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
