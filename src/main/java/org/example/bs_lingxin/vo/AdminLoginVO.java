package org.example.bs_lingxin.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLoginVO implements Serializable {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private String token;
}
