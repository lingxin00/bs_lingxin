package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

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
