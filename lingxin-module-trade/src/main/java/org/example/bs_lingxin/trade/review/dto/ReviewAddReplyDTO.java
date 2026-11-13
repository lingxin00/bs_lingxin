package org.example.bs_lingxin.trade.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 评价回复入参
 *
 * 数据方向：前端 → 后端。接收前端提交的父评价 ID、回复内容和图片，用于商家或用户回复评价。
 */
@Data
public class ReviewAddReplyDTO implements Serializable {
    @NotNull(message = "父评价id不能为空")
    private Long parentReviewId;
    private String content;
    private List<String> images;
}
