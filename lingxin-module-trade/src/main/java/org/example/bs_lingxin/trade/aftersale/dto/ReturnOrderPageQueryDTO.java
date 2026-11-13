package org.example.bs_lingxin.trade.aftersale.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 售后分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。用于分页检索售后单。
 */
@Data
public class ReturnOrderPageQueryDTO extends BasePageQueryDTO implements Serializable {

}
