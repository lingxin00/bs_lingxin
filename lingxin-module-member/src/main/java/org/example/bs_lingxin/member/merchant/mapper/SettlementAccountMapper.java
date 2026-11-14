package org.example.bs_lingxin.member.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountPageQueryDTO;
import org.example.bs_lingxin.member.merchant.entity.SettlementAccount;
import org.example.bs_lingxin.member.merchant.vo.SettlementAccountVO;

import java.util.List;

/**
 * 商家结算账户 Mapper
 */
@Mapper
public interface SettlementAccountMapper extends BaseMapper<SettlementAccount> {

    /**
     * Offset-Limit 分页查询，offset 由 service 计算
     * 关联 merchant 表带出 merchant_name，返回 VO 而不是实体
     */
    List<SettlementAccountVO> selectSettlementAccountPageOffset(@Param("dto") SettlementAccountPageQueryDTO dto,
                                                                @Param("offset") Long offset);

    /**
     * 查询筛选后的总条数，条件与分页查询完全一致
     */
    Long selectSettlementAccountCount(@Param("dto") SettlementAccountPageQueryDTO dto);

    /**
     * 按商家ID查询该商家全部结算账户
     */
    List<SettlementAccountVO> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 按主键查询详情
     */
    SettlementAccountVO selectSettlementAccountById(@Param("id") Long id);
}
