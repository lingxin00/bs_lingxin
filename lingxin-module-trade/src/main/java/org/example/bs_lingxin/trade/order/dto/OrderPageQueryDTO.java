package org.example.bs_lingxin.trade.order.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 订单分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按订单状态分页检索订单。
 */
@Data
public class OrderPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Integer orderStatus;
}
