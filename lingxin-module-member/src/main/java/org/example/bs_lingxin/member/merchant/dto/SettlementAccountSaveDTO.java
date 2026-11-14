package org.example.bs_lingxin.member.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商家结算账户新增 / 修改入参
 *
 * 数据方向：前端 → 后端。id 为空走新增，id 有值走修改。
 */
@Data
public class SettlementAccountSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;

    @NotNull(message = "所属商家ID不能为空")
    private Long merchantId;

    @NotBlank(message = "开户人姓名不能为空")
    private String accountName;

    @NotBlank(message = "收款账号不能为空")
    private String accountNumber;

    //开户行，仅银行卡（accountType=1）填写
    private String bankName;

    @NotNull(message = "账户类型不能为空")
    private Integer accountType; //1银行卡 2支付宝 3微信

    private Integer isDefault;   //1默认结算账户 0否

    private Integer status;      //1正常可用 0停用
}
