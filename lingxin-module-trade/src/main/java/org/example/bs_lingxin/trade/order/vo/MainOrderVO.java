package org.example.bs_lingxin.trade.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主订单展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的主订单信息，subOrderList 内嵌各商家的子订单。
 * addressSnapshot 以 JSON 对象形式返回，前端可直接读取收货人、电话、地址等快照字段。
 */
@Data
public class MainOrderVO implements Serializable {
    private Long id;
    private String orderNo;
    private Long userId;
    private Map<String, Object> addressSnapshot;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer orderStatus;
    private Integer payStatus;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
    private List<SubOrderVO> subOrderList;
}
