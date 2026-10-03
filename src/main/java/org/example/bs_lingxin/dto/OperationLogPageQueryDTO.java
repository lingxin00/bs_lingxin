package org.example.bs_lingxin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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