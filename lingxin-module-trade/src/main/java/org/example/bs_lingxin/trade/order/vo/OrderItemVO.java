package org.example.bs_lingxin.trade.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单明细展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的下单商品快照（规格、单价、数量、小计）。
 * skuSnapshot 以 JSON 对象形式返回，商品后续改价改规格都不影响历史订单展示。
 */
@Data
public class OrderItemVO implements Serializable {
    private Long id;
    private Long subOrderId;
    private Long skuId;
    private Map<String, Object> skuSnapshot;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
