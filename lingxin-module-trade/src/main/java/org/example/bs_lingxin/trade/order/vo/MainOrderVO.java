package org.example.bs_lingxin.trade.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 主订单展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的主订单信息，subOrderList 内嵌各商家的子订单。
 */
@Data
public class MainOrderVO implements Serializable {
    private Long id;
    private String orderNo;
    private Long userId;
    private String addressSnapshot;
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
