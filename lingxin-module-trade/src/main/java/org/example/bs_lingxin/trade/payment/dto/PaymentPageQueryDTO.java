package org.example.bs_lingxin.trade.payment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支付流水分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。分页由父类的 current / size 控制。
 * userId 不放在这里，由 Service 从 token 解析后写入查询条件。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class PaymentPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //订单号，精确匹配
    private String orderNo;
    //支付状态 0待付 1成功 2失败 3已退款
    private Integer payStatus;
    //支付方式 WECHAT / ALIPAY / BALANCE
    private String payMethod;
}
