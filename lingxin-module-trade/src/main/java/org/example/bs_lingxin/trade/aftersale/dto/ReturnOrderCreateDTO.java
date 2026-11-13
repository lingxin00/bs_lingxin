package org.example.bs_lingxin.trade.aftersale.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 售后申请入参
 *
 * 数据方向：前端 → 后端。接收前端提交的退货类型、原因、凭证图片和退款金额，用于发起售后。
 */
@Data
public class ReturnOrderCreateDTO implements Serializable {
    @NotNull(message = "订单项id不能为空")
    private Long orderItemId;
    @NotNull(message = "售后类型不能为空")
    private Integer returnType;
    private String reason;
    private String description;
    private List<String> images;
    private BigDecimal returnAmount;
}
