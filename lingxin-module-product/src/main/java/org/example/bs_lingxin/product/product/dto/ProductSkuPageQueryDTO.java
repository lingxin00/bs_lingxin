package org.example.bs_lingxin.product.product.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * SKU 分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按所属商品、状态、是否促销分页检索 SKU。
 */
@Data
public class ProductSkuPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long productId;
    private Integer status;
    private Integer isPromotion;
}
