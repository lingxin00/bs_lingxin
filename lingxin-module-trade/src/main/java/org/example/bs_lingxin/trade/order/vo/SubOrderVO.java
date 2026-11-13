package org.example.bs_lingxin.trade.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 子订单展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的子订单信息，itemList 内嵌该商家的订单明细。
 */
@Data
public class SubOrderVO implements Serializable {
    private Long id;
    private String subOrderNo;
    private Long mainOrderId;
    private Long merchantId;
    private String merchantName;
    private BigDecimal totalAmount;
    private Integer shippingStatus;
    private String logisticsNo;
    private String logisticsCompany;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;
    private List<OrderItemVO> itemList;
}
