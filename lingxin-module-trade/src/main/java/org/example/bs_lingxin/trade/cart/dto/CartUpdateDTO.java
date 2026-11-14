package org.example.bs_lingxin.trade.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改购物车项入参
 *
 * 数据方向：前端 → 后端。对应 PUT /cart/{id}，只允许改数量和勾选状态。
 * 两个字段都可为 null：为 null 表示该项不修改。
 */
@Data
public class CartUpdateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @Min(value = 1, message = "购买数量不能小于1")
    private Integer quantity;

    private Integer selected; //1下单勾选 0未选中
}
