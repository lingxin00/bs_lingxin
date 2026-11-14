package org.example.bs_lingxin.member.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountSaveDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;
import org.example.bs_lingxin.member.merchant.entity.SettlementAccount;
import org.example.bs_lingxin.member.merchant.mapper.MerchantMapper;
import org.example.bs_lingxin.member.merchant.mapper.SettlementAccountMapper;
import org.example.bs_lingxin.member.merchant.service.SettlementAccountService;
import org.example.bs_lingxin.member.merchant.vo.SettlementAccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SettlementAccountServiceImpl implements SettlementAccountService {

    /** 限制最大跳转页码，防止深分页拖垮数据库 */
    private static final long MAX_ALLOW_PAGE = 50;
    /** 账户类型：1 银行卡，只有银行卡才要求填开户行 */
    private static final int ACCOUNT_TYPE_BANK_CARD = 1;
    private static final int IS_DEFAULT_YES = 1;
    private static final int STATUS_NORMAL = 1;

    private final SettlementAccountMapper settlementAccountMapper;
    private final MerchantMapper merchantMapper;

    public SettlementAccountServiceImpl(SettlementAccountMapper settlementAccountMapper,
                                        MerchantMapper merchantMapper) {
        this.settlementAccountMapper = settlementAccountMapper;
        this.merchantMapper = merchantMapper;
    }

    /**
     * 分页查询
     */
    @Override
    public List<SettlementAccountVO> selectPage(SettlementAccountPageQueryDTO dto) {
        Long current = dto.getCurrent();
        Long size = dto.getSize();
        if (current == null || current < 1) {
            throw new BusinessException("页码不能小于1", 400);
        }
        if (size == null || size < 1) {
            throw new BusinessException("每页条数不能小于1", 400);
        }
        if (current > MAX_ALLOW_PAGE) {
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集", 400);
        }
        Long offset = (current - 1) * size;
        return settlementAccountMapper.selectSettlementAccountPageOffset(dto, offset);
    }

    /**
     * 查询某个商家下全部结算账户
     */
    @Override
    public List<SettlementAccountVO> selectByMerchantId(Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new BusinessException("商家ID不正确", 400);
        }
        return settlementAccountMapper.selectByMerchantId(merchantId);
    }

    /**
     * 按ID查询详情
     */
    @Override
    public SettlementAccountVO selectId(Long id) {
        SettlementAccountVO vo = settlementAccountMapper.selectSettlementAccountById(id);
        if (vo == null) {
            throw new BusinessException("结算账户不存在", 404);
        }
        return vo;
    }

    /**
     * 新增结算账户
     */
    @Override
    public void add(SettlementAccountSaveDTO dto) {
        checkMerchantExists(dto.getMerchantId());
        checkBankName(dto);

        SettlementAccount account = new SettlementAccount();
        BeanUtils.copyProperties(dto, account);
        // 新增忽略前端传来的 id，交给数据库自增
        account.setId(null);
        if (account.getStatus() == null) {
            account.setStatus(STATUS_NORMAL);
        }
        if (account.getIsDefault() == null) {
            account.setIsDefault(0);
        }
        // 设为默认时必须先把该商家其他账户的默认标记清掉
        if (account.getIsDefault() == IS_DEFAULT_YES) {
            clearDefault(account.getMerchantId());
        }
        if (settlementAccountMapper.insert(account) < 1) {
            throw new BusinessException("新增结算账户失败，数据库未写入数据");
        }
    }

    /**
     * 修改结算账户，id 以路径参数为准（忽略 DTO 里的 id）
     */
    @Override
    public void update(Long id, SettlementAccountSaveDTO dto) {
        SettlementAccount account = settlementAccountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException("结算账户不存在", 404);
        }
        checkMerchantExists(dto.getMerchantId());
        checkBankName(dto);

        BeanUtils.copyProperties(dto, account);
        // 路径参数优先，避免前端把 id 传成别的账户
        account.setId(id);
        if (account.getStatus() == null) {
            account.setStatus(STATUS_NORMAL);
        }
        if (account.getIsDefault() == null) {
            account.setIsDefault(0);
        }
        if (account.getIsDefault() == IS_DEFAULT_YES) {
            clearDefault(account.getMerchantId());
        }
        if (settlementAccountMapper.updateById(account) == 0) {
            throw new BusinessException("修改结算账户失败，数据库未写入数据");
        }
    }

    /**
     * 物理删除（表没有 is_deleted，deleteById 就是 DELETE）
     */
    @Override
    public void delete(Long id) {
        SettlementAccount account = settlementAccountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException("结算账户不存在", 404);
        }
        if (settlementAccountMapper.deleteById(id) == 0) {
            throw new BusinessException("删除结算账户失败，数据库未删除数据");
        }
    }

    /**
     * 校验商家存在
     */
    private void checkMerchantExists(Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new BusinessException("所属商家ID不正确", 400);
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家不存在", 400);
        }
    }

    /**
     * 银行卡（accountType=1）必须填开户行；支付宝/微信不需要
     */
    private void checkBankName(SettlementAccountSaveDTO dto) {
        if (dto.getAccountType() != null
                && dto.getAccountType() == ACCOUNT_TYPE_BANK_CARD
                && !StringUtils.hasText(dto.getBankName())) {
            throw new BusinessException("银行卡账户必须填写开户行", 400);
        }
    }

    /**
     * 把该商家下所有账户的 is_default 置 0，保证同一商家只有一个默认账户
     */
    private void clearDefault(Long merchantId) {
        LambdaUpdateWrapper<SettlementAccount> wrapper = Wrappers.lambdaUpdate(SettlementAccount.class)
                .eq(SettlementAccount::getMerchantId, merchantId)
                .set(SettlementAccount::getIsDefault, 0);
        settlementAccountMapper.update(null, wrapper);
    }
}
