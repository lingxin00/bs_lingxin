package org.example.bs_lingxin.product.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 促销活动新增 / 修改入参
 *
 * 数据方向：前端 → 后端。id 为空走新增，id 有值走修改。
 * ruleConfig 直接接收 JSON 对象，与实体 Promotion.ruleConfig 保持一致。
 */
@Data
public class PromotionSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;

    @NotBlank(message = "活动名称不能为空")
    private String promotionName;

    private String promotionCode;

    @NotNull(message = "活动类型不能为空")
    private Integer promotionType; //1满减 2折扣 3秒杀 4优惠券

    //活动规则JSON，如 {"threshold":100,"discount":20}
    private Map<String, Object> ruleConfig;

    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime endTime;

    private Integer status; //0未开始 1进行中 2结束 3取消
}
