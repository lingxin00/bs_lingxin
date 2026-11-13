package org.example.bs_lingxin.product.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类新增 / 修改入参
 *
 * 数据方向：前端 → 后端。接收前端提交的分类表单（名称、编码、图标、排序、状态）。
 */
@Data
public class CategorySaveDTO implements Serializable {
    private Long id;
    private Long parentId;
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private String categoryCode;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
