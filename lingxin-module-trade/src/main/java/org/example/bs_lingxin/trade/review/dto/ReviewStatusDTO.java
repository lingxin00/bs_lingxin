package org.example.bs_lingxin.trade.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评价显示状态修改入参
 *
 * 数据方向：前端 → 后端。对应 PUT /review/{id}/status，管理员隐藏或恢复评价展示。
 */
@Data
public class ReviewStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotNull(message = "状态不能为空")
    private Integer status; //1正常展示 0管理员隐藏
}
