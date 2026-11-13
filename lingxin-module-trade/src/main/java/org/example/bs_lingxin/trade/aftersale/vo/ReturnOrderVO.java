package org.example.bs_lingxin.trade.aftersale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 售后单展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的售后单信息，含审核结果和凭证图片。
 */
@Data
public class ReturnOrderVO implements Serializable {
    private Long id;
    private String returnNo;
    private Long orderItemId;
    private Long userId;
    private String nickname;
    private Integer returnType;
    private String reason;
    private String description;
    private List<String> images;
    private BigDecimal returnAmount;
    private Integer status;
    private String auditRemark;
    private Long auditorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveGoodsTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
