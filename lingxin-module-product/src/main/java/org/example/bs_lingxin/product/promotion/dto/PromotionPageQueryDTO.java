package org.example.bs_lingxin.product.promotion.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * 促销活动分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。分页由父类的 current / size 控制。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class PromotionPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //活动名称，模糊匹配
    private String promotionName;
    //活动类型 1满减 2折扣 3秒杀 4优惠券
    private Integer promotionType;
    //活动状态 0未开始 1进行中 2结束 3取消
    private Integer status;
}
