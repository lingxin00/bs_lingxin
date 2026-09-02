package org.example.bs_lingxin.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MerchantLoginVO implements Serializable {
    private Long id;
    private String username;
    private String merchantName;
    private String logo;
    private String token;
}
