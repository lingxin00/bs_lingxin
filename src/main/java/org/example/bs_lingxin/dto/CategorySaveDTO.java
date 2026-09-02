package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

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
