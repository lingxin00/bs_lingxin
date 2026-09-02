package org.example.bs_lingxin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime createTime;
}
