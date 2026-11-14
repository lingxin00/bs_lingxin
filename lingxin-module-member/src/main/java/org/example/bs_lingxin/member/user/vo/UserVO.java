package org.example.bs_lingxin.member.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.bs_lingxin.member.user.entity.User;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的用户信息，已按展示需要脱敏，不含密码。
 */
@Data
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

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

    /**
     * 实体转 VO，只挑展示字段，password 不外泄
     */
    public UserVO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.nickname = user.getNickname();
        this.status = user.getStatus();
        this.lastLoginTime = user.getLastLoginTime();
        this.createTime = user.getCreateTime();
    }
}
