package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReviewCreateDTO implements Serializable {
    @NotNull(message = "订单项id不能为空")
    private Long orderItemId;
    @NotNull(message = "评分不能为空")
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
}
