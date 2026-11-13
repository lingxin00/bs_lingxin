package org.example.bs_lingxin.system.role.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的角色信息，用于角色列表和详情展示。
 */
@Data
public class RoleVO implements Serializable {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
