package org.example.bs_lingxin.member.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改资料入参
 *
 * 数据方向：前端 → 后端。接收前端提交的个人资料（昵称、头像、邮箱），用于用户自助修改。
 */
@Data
public class UserProfileDTO implements Serializable {
    private String nickname;
    private String avatar;
    private String email;
}
