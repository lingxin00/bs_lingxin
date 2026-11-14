package org.example.bs_lingxin.member.merchant.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.SettlementAccountSaveDTO;
import org.example.bs_lingxin.member.merchant.mapper.SettlementAccountMapper;
import org.example.bs_lingxin.member.merchant.service.SettlementAccountService;
import org.example.bs_lingxin.member.merchant.vo.SettlementAccountVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家结算账户接口
 */
@Slf4j
@RestController
@RequestMapping("/api/settlement-account")
public class SettlementAccountController {

    private final SettlementAccountService settlementAccountService;
    private final SettlementAccountMapper settlementAccountMapper;

    public SettlementAccountController(SettlementAccountService settlementAccountService,
                                       SettlementAccountMapper settlementAccountMapper) {
        this.settlementAccountService = settlementAccountService;
        this.settlementAccountMapper = settlementAccountMapper;
    }

    /**
     * 分页查询结算账户（merchantId 非必填，不传查全部）
     */
    @GetMapping("/list")
    public Result<?> list(@Valid SettlementAccountPageQueryDTO dto) {
        List<SettlementAccountVO> records = settlementAccountService.selectPage(dto);
        Long total = settlementAccountMapper.selectSettlementAccountCount(dto);
        return Result.success(buildPage(records, total, dto.getCurrent(), dto.getSize()));
    }

    /**
     * 根据商家ID查询该商家所有结算账户
     */
    @GetMapping("/getByMerchantId")
    public Result<?> getByMerchantId(@RequestParam Long merchantId) {
        return Result.success(settlementAccountService.selectByMerchantId(merchantId));
    }

    /**
     * 根据ID查询账户详情
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(settlementAccountService.selectId(id));
    }

    /**
     * 新增结算账户
     */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody SettlementAccountSaveDTO dto) {
        settlementAccountService.add(dto);
        log.info("新增结算账户成功，merchantId={}，accountType={}", dto.getMerchantId(), dto.getAccountType());
        return Result.success();
    }

    /**
     * 修改结算账户
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody SettlementAccountSaveDTO dto) {
        settlementAccountService.update(id, dto);
        log.info("修改结算账户成功，id={}", id);
        return Result.success();
    }

    /**
     * 物理删除结算账户（settlement_account 表没有 is_deleted）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        settlementAccountService.delete(id);
        log.info("删除结算账户成功，id={}", id);
        return Result.success();
    }

    /**
     * 拼装分页返回结构，与 MerchantController.selectPage 保持一致
     */
    private Map<String, Object> buildPage(List<?> records, Long total, Long current, Long size) {
        long pages = 0L;
        if (total != null && total > 0) {
            pages = total % size == 0 ? total / size : total / size + 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        // 空结果也是正常业务，返回空列表 + total=0，不当作错误
        map.put("total", total == null ? 0L : total);
        map.put("pages", pages);
        map.put("current", current);
        map.put("size", size);
        return map;
    }
}
