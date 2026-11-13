package org.example.bs_lingxin.member.merchant.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家展示对象
 *
 * 数据方向：后端 → 前端。返回给前端的商家信息，不含密码等敏感字段。
 */
@Data
public class MerchantVO implements Serializable {
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
}
