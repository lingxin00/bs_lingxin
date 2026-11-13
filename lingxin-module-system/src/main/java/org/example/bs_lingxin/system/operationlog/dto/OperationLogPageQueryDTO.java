package org.example.bs_lingxin.system.operationlog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 操作日志分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按操作人、模块、操作类型和时间范围分页检索日志。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperationLogPageQueryDTO extends BasePageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long adminId;
    private String module;
    private String operationType;
    private String operationDescLike;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;


}