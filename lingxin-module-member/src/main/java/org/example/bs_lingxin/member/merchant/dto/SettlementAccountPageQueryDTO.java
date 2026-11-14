package org.example.bs_lingxin.member.merchant.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商家结算账户分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按商家ID筛选结算账户，分页由父类的 current / size 控制。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class SettlementAccountPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //所属商家ID，null查全部
    private Long merchantId;
}
