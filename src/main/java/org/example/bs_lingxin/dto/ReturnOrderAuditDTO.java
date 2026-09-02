package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnOrderAuditDTO implements Serializable {
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    private String auditRemark;
}
