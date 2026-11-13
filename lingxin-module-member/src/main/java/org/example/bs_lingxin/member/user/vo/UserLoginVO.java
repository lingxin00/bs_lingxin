package org.example.bs_lingxin.member.user.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录结果
 *
 * 数据方向：后端 → 前端。登录成功后返回给前端的用户资料与 token。
 */
@Data
public class UserLoginVO implements Serializable {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String token;
}
