package org.example.bs_lingxin.service;

import org.example.bs_lingxin.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.entity.OperationLog;


import java.util.List;

public interface OperationLogService {

    void saveAsync(OperationLog operationLog);

    List<OperationLog> pageQuery(OperationLogPageQueryDTO dto);
}

