package org.example.bs_lingxin.product.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品图片新增 / 修改入参
 *
 * 数据方向：前端 → 后端。product_image 表无逻辑删除，删除即物理删除。
 */
@Data
public class ProductImageSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;

    @NotNull(message = "所属商品ID不能为空")
    private Long productId;

    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    private Integer isMain;    //1为主图 0普通轮播图
    private Integer sortOrder; //图片排序
}
