package org.example.bs_lingxin.trade.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单入参
 *
 * 数据方向：前端 → 后端。接收前端提交的收货地址、订单备注和购物车条目 ID 列表，用于创建订单。
 */
@Data
public class OrderCreateDTO implements Serializable {
    @NotNull(message = "收货地址id不能为空")
    private Long addressId;
    private String remark;
    @NotEmpty(message = "请选择要下单的商品")
    private List<Long> cartItemIds;
}
