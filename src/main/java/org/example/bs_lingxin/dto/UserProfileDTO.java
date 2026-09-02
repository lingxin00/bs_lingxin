package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileDTO implements Serializable {
    private String nickname;
    private String avatar;
    private String email;
}
