package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员查询条件DTO
 */
@Data
public class AdminQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //精确用户名
    private String username;
    //用户名模糊
    private String usernameLike;
    //真实姓名模糊
    private String realNameLike;
    //手机号精确
    private String phone;
    //状态 0禁用 1正常
    private Integer status;
    //角色id
    private Long roleId;
    //逻辑删除：0未删，1已删，null全部
    private Integer isDeleted;

    //可选时间范围筛选
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
    private LocalDateTime lastLoginTimeStart;
    private LocalDateTime lastLoginTimeEnd;
}
