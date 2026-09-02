package org.example.bs_lingxin.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductImageVO implements Serializable {
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer isMain;
    private Integer sortOrder;
}
