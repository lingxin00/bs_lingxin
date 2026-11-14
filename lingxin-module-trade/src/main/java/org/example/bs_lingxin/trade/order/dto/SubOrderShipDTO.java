package org.example.bs_lingxin.trade.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商家发货入参
 *
 * 数据方向：前端 → 后端。对应 POST /sub-order/{id}/ship。
 */
@Data
public class SubOrderShipDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotBlank(message = "快递单号不能为空")
    private String logisticsNo;

    @NotBlank(message = "快递公司不能为空")
    private String logisticsCompany;
}
