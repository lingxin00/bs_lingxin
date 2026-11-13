package org.example.bs_lingxin.product.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品图片展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的商品图片信息，isMain 标识主图。
 */
@Data
public class ProductImageVO implements Serializable {
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer isMain;
    private Integer sortOrder;
}
