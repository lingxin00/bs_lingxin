package org.example.bs_lingxin.member.merchant.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 商家分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按商家名称和状态分页检索商家。
 */
@Data
public class MerchantPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String merchantName;
    private Integer status;
}
