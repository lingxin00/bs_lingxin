package org.example.bs_lingxin.product.promotion.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 促销活动展示对象
 *
 * 数据方向：后端 → 前端。skuCount 为跨表统计字段，由 Service 查询时填充。
 */
@Data
public class PromotionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private String promotionName;
    private String promotionCode;
    private Integer promotionType; //1满减 2折扣 3秒杀 4优惠券
    private Map<String, Object> ruleConfig;
    private Integer status;        //0未开始 1进行中 2结束 3取消

    //跨表字段：绑定到本活动的SKU数量
    private Integer skuCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
