package org.example.bs_lingxin.trade.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 提交评价入参
 *
 * 数据方向：前端 → 后端。接收前端提交的评分、评价内容、图片和是否匿名，用于对订单明细发表评价。
 */
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
