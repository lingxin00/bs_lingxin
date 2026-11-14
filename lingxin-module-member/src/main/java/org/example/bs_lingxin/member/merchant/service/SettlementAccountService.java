package org.example.bs_lingxin.member.merchant.service;

import org.example.bs_lingxin.member.merchant.dto.SettlementAccountPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountSaveDTO;
import org.example.bs_lingxin.member.merchant.vo.SettlementAccountVO;

import java.util.List;

/**
 * 商家结算账户服务
 */
public interface SettlementAccountService {

    /**
     * 分页查询结算账户（可只查某个商家）
     */
    List<SettlementAccountVO> selectPage(SettlementAccountPageQueryDTO dto);

    /**
     * 查询某个商家下全部结算账户
     */
    List<SettlementAccountVO> selectByMerchantId(Long merchantId);

    /**
     * 按ID查询结算账户详情
     */
    SettlementAccountVO selectId(Long id);

    /**
     * 新增结算账户
     */
    void add(SettlementAccountSaveDTO dto);

    /**
     * 修改结算账户，id 以路径参数为准
     */
    void update(Long id, SettlementAccountSaveDTO dto);

    /**
     * 物理删除结算账户（表无 is_deleted）
     */
    void delete(Long id);
}
