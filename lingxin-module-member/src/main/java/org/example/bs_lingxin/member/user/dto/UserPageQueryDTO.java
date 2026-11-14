package org.example.bs_lingxin.member.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 用户分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。接收前端 URL 查询参数（页码、每页条数 + 手机号、状态），用于后台分页查用户。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class UserPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //手机号，精确匹配
    private String phone;

    //状态 1正常 0封禁
    private Integer status;
}
