package org.example.bs_lingxin.trade.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 购物车全选 / 取消全选入参
 *
 * 数据方向：前端 → 后端。对应 PUT /cart/checkAll，userId 由 token 解析。
 */
@Data
public class CartCheckAllDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotNull(message = "勾选状态不能为空")
    private Integer selected; //1全选 0取消全选
}
