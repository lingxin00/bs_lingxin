package org.example.bs_lingxin.member.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户状态修改入参
 *
 * 数据方向：前端 → 后端。对应 PUT /user/status/{id} 的请求体。
 */
@Data
public class UserStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    @NotNull(message = "状态不能为空")
    private Integer status; //1正常 0封禁
}
