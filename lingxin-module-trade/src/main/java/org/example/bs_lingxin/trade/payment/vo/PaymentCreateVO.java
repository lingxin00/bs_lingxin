package org.example.bs_lingxin.trade.payment.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 生成支付单返回对象
 *
 * 数据方向：后端 → 前端。前端拿 paymentNo 轮询支付结果，拿 payParams 拉起第三方支付。
 */
@Data
public class PaymentCreateVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private String paymentNo;
    private String orderNo;
    private BigDecimal payAmount;
    private String payMethod; //WECHAT / ALIPAY / BALANCE

    //支付跳转参数：微信返回 prepay_id 等，支付宝返回表单串，前端直接使用
    private Map<String, Object> payParams;
}
