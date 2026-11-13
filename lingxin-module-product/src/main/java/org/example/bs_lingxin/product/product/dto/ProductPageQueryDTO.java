package org.example.bs_lingxin.product.product.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 商品分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按分类、商家、商品名称和状态分页检索商品。
 */
@Data
public class ProductPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long categoryId;
    private Long merchantId;
    private String productName;
    private Integer status;
}
