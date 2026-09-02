package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReviewAddReplyDTO implements Serializable {
    @NotNull(message = "父评价id不能为空")
    private Long parentReviewId;
    private String content;
    private List<String> images;
}
