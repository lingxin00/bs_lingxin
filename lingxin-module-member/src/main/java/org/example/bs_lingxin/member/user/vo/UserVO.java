package org.example.bs_lingxin.member.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的用户信息，已按展示需要脱敏，不含密码。
 */
@Data
public class UserVO implements Serializable {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
