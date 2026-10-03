package org.example.bs_lingxin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.bs_lingxin.dto.OperationLogPageQueryDTO;
import org.example.bs_lingxin.entity.OperationLog;


import java.util.List;

@Mapper
public interface OperationLogMapper {

    /** 新增操作日志 */
    int insert(OperationLog operationLog);

    /**
     * 分页查询操作日志（Offset 方式）
     * @param dto 查询条件（含 offset、size 由 Service 设置）
     * @return 日志列表
     */
    List<OperationLog> selectOperationLogPageOffset(OperationLogPageQueryDTO dto);
}


