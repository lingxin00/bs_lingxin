package org.example.bs_lingxin.trade.aftersale.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 售后审核入参
 *
 * 数据方向：前端 → 后端。接收前端提交的审核结果（通过 / 驳回）和审核备注。
 */
@Data
public class ReturnOrderAuditDTO implements Serializable {
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    private String auditRemark;
}
