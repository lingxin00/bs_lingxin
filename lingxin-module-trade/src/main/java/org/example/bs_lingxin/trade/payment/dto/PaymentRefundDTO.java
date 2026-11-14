package org.example.bs_lingxin.trade.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发起退款入参
 *
 * 数据方向：前端 → 后端。对应 POST /payment/refund。
 */
@Data
public class PaymentRefundDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0")
    private BigDecimal refundAmount;

    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
