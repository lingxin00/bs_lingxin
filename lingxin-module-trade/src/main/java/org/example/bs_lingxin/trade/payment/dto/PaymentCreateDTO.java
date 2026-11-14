package org.example.bs_lingxin.trade.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成支付单入参
 *
 * 数据方向：前端 → 后端。对应 POST /payment/create。
 */
@Data
public class PaymentCreateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotBlank(message = "支付方式不能为空")
    private String payMethod; //WECHAT / ALIPAY / BALANCE
}
