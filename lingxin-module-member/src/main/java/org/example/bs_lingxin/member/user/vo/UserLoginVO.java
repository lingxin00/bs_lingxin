package org.example.bs_lingxin.member.user.vo;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;
import org.example.bs_lingxin.member.user.entity.User;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录结果
 *
 * 数据方向：后端 → 前端。登录成功后返回给前端的用户资料与 token。
 * 注意：不含 password，避免密码哈希被序列化到前端。
 */
@Data
public class UserLoginVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private String username;
    private String phone;
    private String nickname;
    private String avatar;

    // token，前端后续请求头携带，必须保留
    private String token;

    /**
     * 构造方法，把 user 和 tokenInfo 赋值
     */
    public UserLoginVO(User user, SaTokenInfo tokenInfo) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
        this.token = tokenInfo.getTokenValue();
    }
}
