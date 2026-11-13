package org.example.bs_lingxin.product.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的商品详情，比数据库实体多 categoryName、merchantName 便于列表直接展示。
 */
@Data
public class ProductVO implements Serializable {
    private Long id;
    private String productName;
    private String productCode;
    private Long categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String brand;
    private String description;
    private String mainImage;
    private String detailHtml;
    private Integer status;
    private Integer sortOrder;
    private List<ProductImageVO> imageList;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
