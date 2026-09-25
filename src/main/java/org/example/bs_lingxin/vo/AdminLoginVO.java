package org.example.bs_lingxin.vo;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;
import org.example.bs_lingxin.entity.Admin;

import java.io.Serializable;

@Data
public class AdminLoginVO implements Serializable {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private String token;

    // 新增构造方法，用来把admin、token数据赋值进来
    public AdminLoginVO(Admin admin, SaTokenInfo tokenInfo) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.realName = admin.getRealName();
        this.avatar = admin.getAvatar();
        this.token = tokenInfo.getTokenValue();
    }
}
