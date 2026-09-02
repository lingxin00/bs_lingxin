package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreateDTO implements Serializable {
    @NotNull(message = "收货地址id不能为空")
    private Long addressId;
    private String remark;
    @NotEmpty(message = "请选择要下单的商品")
    private List<Long> cartItemIds;
}
