package org.example.bs_lingxin.trade.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 添加购物车入参
 *
 * 数据方向：前端 → 后端。userId 由 token 解析后由 Service 写入，不在入参中传递。
 * 同一用户同一 SKU 只保留一条记录，重复添加时累加数量。
 */
@Data
public class CartAddDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer quantity;
}
