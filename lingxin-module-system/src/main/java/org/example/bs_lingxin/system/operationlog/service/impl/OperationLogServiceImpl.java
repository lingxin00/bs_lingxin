package org.example.bs_lingxin.system.operationlog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.system.operationlog.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.system.operationlog.entity.OperationLog;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.system.operationlog.mapper.OperationLogMapper;
import org.example.bs_lingxin.system.operationlog.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    /** 限制最大跳转页码（与 AdminServiceImpl 保持一致） */
    private static final long MAX_ALLOW_PAGE = 50;

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 异步新增操作日志
     *
     * 异步线程的异常不会传播给主线程，只能在方法内自行记录，不能向外抛。
     */
    @Async("logExecutor")
    @Override
    public void saveAsync(OperationLog operationLog) {
        try {
            int rows = operationLogMapper.insert(operationLog);
            if (rows < 1) {
                log.warn("操作日志未写入数据库 module={} type={} desc={} url={}",
                        operationLog.getModule(), operationLog.getOperationType(),
                        operationLog.getOperationDesc(), operationLog.getRequestUrl());
            }
        } catch (Exception e) {
            log.error("保存操作日志失败 module={} type={} desc={} url={}",
                    operationLog.getModule(), operationLog.getOperationType(),
                    operationLog.getOperationDesc(), operationLog.getRequestUrl(), e);
        }
    }

    /**
     * 分页查询操作日志
     */
    @Override
    public List<OperationLog> pageQuery(OperationLogPageQueryDTO dto) {
        Long current = dto.getCurrent();
        Long size = dto.getSize();

        if (current == null || current < 1) {
            throw new BusinessException("页码不能小于1");
        }
        if (size == null || size < 1) {
            throw new BusinessException("每页条数不能小于1");
        }
        if (current > MAX_ALLOW_PAGE) {
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集");
        }

        List<OperationLog> logs = operationLogMapper.selectOperationLogPageOffset(dto);

        if (logs == null) {
            throw new BusinessException("操作日志数据为空");
        }
        return logs;
    }
}
