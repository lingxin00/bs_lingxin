package org.example.bs_lingxin.member.merchant.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家登录结果
 *
 * 数据方向：后端 → 前端。登录成功后返回给前端的商家资料与 token。
 */
@Data
public class MerchantLoginVO implements Serializable {
    private Long id;
    private String username;
    private String merchantName;
    private String logo;
    private String token;
}
