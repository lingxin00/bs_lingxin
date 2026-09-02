package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReturnOrderCreateDTO implements Serializable {
    @NotNull(message = "订单项id不能为空")
    private Long orderItemId;
    @NotNull(message = "售后类型不能为空")
    private Integer returnType;
    private String reason;
    private String description;
    private List<String> images;
    private BigDecimal returnAmount;
}
