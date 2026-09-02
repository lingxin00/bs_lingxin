package org.example.bs_lingxin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime shipTime;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime receiveTime;
    private List<OrderItemVO> itemList;
}
