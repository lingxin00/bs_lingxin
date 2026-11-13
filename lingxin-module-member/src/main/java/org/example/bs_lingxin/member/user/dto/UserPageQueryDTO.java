package org.example.bs_lingxin.member.user.dto;

import lombok.Data;

import java.io.Serializable;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 用户分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。接收前端 URL 查询参数（页码、每页条数 + 手机号、状态），用于后台分页查用户。
 */
@Data
public class UserPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private String phone;
    private Integer status;
}
