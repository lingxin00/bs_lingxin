package org.example.bs_lingxin.member.merchant.vo;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;
import org.example.bs_lingxin.member.merchant.entity.Merchant;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家登录结果
 *
 * 数据方向：后端 → 前端。登录成功后返回给前端的商家资料与 token，token 由前端存入请求头。
 * 注意：不含 password，避免密码哈希被序列化到前端。
 */
@Data
public class MerchantLoginVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //基础信息
    private Long id;
    private String username;
    private String merchantName;
    private String logo;

    //经营信息
    private String merchantCode;
    private String address;
    private String description;

    //联系人信息
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    //状态与时间
    private Integer status;
    private LocalDateTime createTime;

    // token，前端后续请求头携带，必须保留
    private String token;

    // 构造方法，把 merchant 和 tokenInfo 赋值
    public MerchantLoginVO(Merchant merchant, SaTokenInfo tokenInfo) {
        this.id = merchant.getId();
        this.username = merchant.getUsername();
        this.merchantName = merchant.getMerchantName();
        this.logo = merchant.getLogo();
        this.merchantCode = merchant.getMerchantCode();
        this.address = merchant.getAddress();
        this.description = merchant.getDescription();
        this.contactName = merchant.getContactName();
        this.contactPhone = merchant.getContactPhone();
        this.contactEmail = merchant.getContactEmail();
        this.status = merchant.getStatus();
        this.createTime = merchant.getCreateTime();
        this.token = tokenInfo.getTokenValue();
    }
}
