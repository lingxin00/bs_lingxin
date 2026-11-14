package org.example.bs_lingxin.system.operationlog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志展示对象
 *
 * 数据方向：后端 → 前端。adminUsername 为跨表字段，由 Service 查询时填充。
 */
@Data
public class OperationLogVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private Long adminId;
    //跨表字段：操作人账号
    private String adminUsername;

    private String operationType; //ADD/UPDATE/DELETE/LOGIN
    private String module;        //操作模块 商品/订单/商家等
    private String operationDesc;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String ip;
    private String userAgent;
    private Integer costTime;     //接口耗时毫秒

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
