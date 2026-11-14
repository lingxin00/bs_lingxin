package org.example.bs_lingxin.member.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户收货地址新增 / 修改入参
 *
 * 数据方向：前端 → 后端。userId 由 token 解析后由 Service 写入，不在入参中传递。
 */
@Data
public class UserAddressSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货手机号不能为空")
    private String receiverPhone;

    private String province;
    private String city;
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    private String postalCode;

    private Integer isDefault; //1默认地址 0否
}
