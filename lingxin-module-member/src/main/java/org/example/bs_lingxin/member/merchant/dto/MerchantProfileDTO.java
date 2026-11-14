package org.example.bs_lingxin.member.merchant.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商家修改自身资料入参
 *
 * 数据方向：前端 → 后端。商家登录后只能改自己，id 由 token 解析，不在入参中传递。
 */
@Data
public class MerchantProfileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //店铺名称
    private String merchantName;
    //商家编码
    private String merchantCode;
    //负责人姓名
    private String contactName;
    //负责人电话
    private String contactPhone;
    //联系邮箱
    private String contactEmail;
    //店铺地址
    private String address;
    //店铺Logo URL
    private String logo;
    //店铺简介
    private String description;

    //新密码，为空表示不修改密码
    private String password;
}
