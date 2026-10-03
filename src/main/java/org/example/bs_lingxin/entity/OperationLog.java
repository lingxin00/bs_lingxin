package org.example.bs_lingxin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 对应数据库表 operation_log
 * 作用：记录管理员后台所有操作，用于审计追溯，无逻辑删除，物理删除
 */
@Data
@TableName(value = "operation_log", autoResultMap = true)
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private String operationType;

    private String module;

    private String operationDesc;

    private String requestUrl;

    private String requestMethod;

    private String requestParams;

    private String ip;

    private String userAgent;

    private Integer costTime;

    private LocalDateTime createTime;
}
