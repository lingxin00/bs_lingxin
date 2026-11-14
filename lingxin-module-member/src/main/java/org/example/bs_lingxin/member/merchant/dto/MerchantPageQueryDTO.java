package org.example.bs_lingxin.member.merchant.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.example.bs_lingxin.common.dto.BasePageQueryDTO;

/**
 * 商家分页查询条件
 *
 * 数据方向：前端 → 后端（URL 查询参数）。按店铺名称、账号、编码、联系人、状态、创建时间范围分页检索商家。
 */
@Data
@EqualsAndHashCode(callSuper = true) // equals、hashCode带上父类字段
@ToString(callSuper = true)         // toString打印带上父类字段
public class MerchantPageQueryDTO extends BasePageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    //店铺名称，模糊匹配
    private String merchantName;
    //商家登录账号，精确匹配
    private String username;
    //商家编码，精确匹配
    private String merchantCode;
    //负责人姓名，精确匹配
    private String contactName;
    //负责人电话，精确匹配
    private String contactPhone;
    //联系邮箱，精确匹配
    private String contactEmail;
    //状态 1正常 0停用禁止登录
    private Integer status;

    //可选时间范围筛选
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
}
