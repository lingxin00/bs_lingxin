package org.example.bs_lingxin.product.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的商品主表（SPU）信息，不含 SKU 明细。
 */
@Data
public class ProductSaveDTO implements Serializable {
    private Long id;
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    private String productCode;
    private Long categoryId;
    private Long merchantId;
    private String brand;
    private String description;
    private String mainImage;
    private String detailHtml;
    private Integer status;
    private Integer sortOrder;
}
