package org.example.bs_lingxin.system.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.bs_lingxin.system.admin.entity.Admin;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的管理员信息，不含密码，比数据库实体多一个 roleName，供列表和详情页展示。
 */
@Data
public class AdminVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private Long roleId;
    private String roleName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 实体转 VO，只挑展示字段，password 不外泄
     * roleName 需要额外查 role 表，由 service 填充
     */
    public AdminVO(Admin admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.realName = admin.getRealName();
        this.phone = admin.getPhone();
        this.email = admin.getEmail();
        this.avatar = admin.getAvatar();
        this.status = admin.getStatus();
        this.roleId = admin.getRoleId();
        this.lastLoginTime = admin.getLastLoginTime();
        this.lastLoginIp = admin.getLastLoginIp();
        this.createTime = admin.getCreateTime();
        this.updateTime = admin.getUpdateTime();
    }
}
