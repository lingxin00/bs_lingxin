package org.example.bs_lingxin.service.impl;

import org.example.bs_lingxin.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.entity.OperationLog;
import org.example.bs_lingxin.exception.BusinessException;
import org.example.bs_lingxin.mapper.OperationLogMapper;
import org.example.bs_lingxin.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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
     */
    @Async("logExecutor")
    @Override
    public void saveAsync(OperationLog operationLog) {
        try {
            int i = operationLogMapper.insert(operationLog);
            if (i < 1) {
                throw new BusinessException("新增操作日志失败，数据库未写入数据");
            }
        } catch (Exception e) {
            // 异步线程异常无法传播到主线程，自行捕获记录
            System.err.println("记录操作日志失败: " + e.getMessage());
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