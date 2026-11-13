package org.example.bs_lingxin.trade.review.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 评价分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按 SKU 分页检索商品评价。
 */
@Data
public class ReviewPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long skuId;
}
