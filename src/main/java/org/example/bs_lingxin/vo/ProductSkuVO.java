package org.example.bs_lingxin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductSkuVO implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private String specAttrs;
    private String skuCode;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer soldCount;
    private LocalDate productionDate;
    private Integer expiryDays;
    private LocalDate expirationDate;
    private Integer warningThreshold;
    private Integer isPromotion;
    private BigDecimal promotionPrice;
    private String images;
    private BigDecimal weight;
    private Integer status;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime createTime;
}
