package org.example.bs_lingxin.vo;

import lombok.Data;
import org.example.bs_lingxin.entity.Admin;
import cn.dev33.satoken.stp.SaTokenInfo;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class AdminLoginVO implements Serializable {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createTime;
    private Long roleId;

    // token，前端后续请求头携带，必须保留
    private String token;

    // 构造方法，把admin和tokenInfo赋值
    public AdminLoginVO(Admin admin, SaTokenInfo tokenInfo) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.realName = admin.getRealName();
        this.phone = admin.getPhone();
        this.email = admin.getEmail();
        this.avatar = admin.getAvatar();
        this.status = admin.getStatus();
        this.lastLoginTime = admin.getLastLoginTime();
        this.lastLoginIp = admin.getLastLoginIp();
        this.createTime = admin.getCreateTime();
        this.roleId = admin.getRoleId();
        this.token = tokenInfo.getTokenValue();
    }
}
