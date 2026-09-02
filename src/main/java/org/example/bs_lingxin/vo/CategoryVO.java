package org.example.bs_lingxin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss")
    private LocalDateTime createTime;
}
