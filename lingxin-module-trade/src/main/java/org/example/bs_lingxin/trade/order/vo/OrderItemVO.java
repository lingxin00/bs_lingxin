package org.example.bs_lingxin.trade.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的下单商品快照（规格、单价、数量、小计）。
 */
@Data
public class OrderItemVO implements Serializable {
    private Long id;
    private Long subOrderId;
    private Long skuId;
    private String skuSnapshot;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
