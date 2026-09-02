package org.example.bs_lingxin.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginVO implements Serializable {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String token;
}
