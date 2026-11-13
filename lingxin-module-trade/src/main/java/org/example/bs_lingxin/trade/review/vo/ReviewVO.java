package org.example.bs_lingxin.trade.review.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的评价内容，含评分、图片和匿名标记。
 */
@Data
public class ReviewVO implements Serializable {
    private Long id;
    private Long orderItemId;
    private Long parentReviewId;
    private Long userId;
    private String nickname;
    private Long skuId;
    private Integer reviewType;
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<ReviewVO> childReviewList;
}
