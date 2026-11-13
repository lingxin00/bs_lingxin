package org.example.bs_lingxin.system.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 管理员分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。接收前端 URL 查询参数（页码、每页条数 + 筛选条件），用于分页检索管理员。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class AdminPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号
    private String username;
    private Integer status;
    //新增逻辑删除参数：0未删，1已删，null全部
    private Integer isDeleted;
    //用户名模糊
    private String usernameLike;
    //真实姓名模糊
    private String realNameLike;
    //手机号精确
    private String phone;
    //角色id
    private Long roleId;

    //可选时间范围筛选
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
    private LocalDateTime lastLoginTimeStart;
    private LocalDateTime lastLoginTimeEnd;
}
