package org.example.bs_lingxin.product.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 分类分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按上级分类 ID 分页检索商品分类。
 */
@Data
public class CategoryPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long id;
    private Long parentId;
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private String categoryCode;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
