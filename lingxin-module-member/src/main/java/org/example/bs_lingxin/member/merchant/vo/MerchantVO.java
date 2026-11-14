package org.example.bs_lingxin.member.merchant.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.bs_lingxin.member.merchant.entity.Merchant;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的商家信息，不含密码等敏感字段。
 */
@Data
public class MerchantVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private String username;
    private String merchantName;
    private String merchantCode;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String logo;
    private String description;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 实体转 VO，只挑展示字段，password 不外泄
     */
    public MerchantVO(Merchant merchant) {
        this.id = merchant.getId();
        this.username = merchant.getUsername();
        this.merchantName = merchant.getMerchantName();
        this.merchantCode = merchant.getMerchantCode();
        this.contactName = merchant.getContactName();
        this.contactPhone = merchant.getContactPhone();
        this.contactEmail = merchant.getContactEmail();
        this.address = merchant.getAddress();
        this.logo = merchant.getLogo();
        this.description = merchant.getDescription();
        this.status = merchant.getStatus();
        this.createTime = merchant.getCreateTime();
        this.updateTime = merchant.getUpdateTime();
    }
}
