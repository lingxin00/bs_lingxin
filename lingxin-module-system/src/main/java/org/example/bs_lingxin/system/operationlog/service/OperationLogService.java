package org.example.bs_lingxin.system.operationlog.service;

import org.example.bs_lingxin.system.operationlog.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.system.operationlog.entity.OperationLog;


import java.util.List;

public interface OperationLogService {

    void saveAsync(OperationLog operationLog);

    List<OperationLog> pageQuery(OperationLogPageQueryDTO dto);
}

