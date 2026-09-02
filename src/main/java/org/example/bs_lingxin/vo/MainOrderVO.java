package org.example.bs_lingxin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime payTime;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime cancelTime;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime finishTime;
    private List<SubOrderVO> subOrderList;
}
