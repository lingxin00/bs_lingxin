package org.example.bs_lingxin.product.category.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的分类信息，children 字段用于拼装多级分类树。
 */
@Data
public class CategoryVO implements Serializable {
    private Long id;
    private Long parentId;
    private String categoryName;
    private String categoryCode;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private List<CategoryVO> children;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
