package org.example.bs_lingxin.system.operationlog.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.common.enums.ResultCode;
import org.example.bs_lingxin.system.operationlog.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.system.operationlog.entity.OperationLog;
import org.example.bs_lingxin.system.operationlog.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/operation-log")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<List<OperationLog>> page(OperationLogPageQueryDTO dto) {
        if (dto == null){
            return Result.fail(ResultCode.BAD_REQUEST, "请求参数不能为空");
        }
        List<OperationLog> list = operationLogService.pageQuery(dto);
        return Result.success(list);
    }

}
