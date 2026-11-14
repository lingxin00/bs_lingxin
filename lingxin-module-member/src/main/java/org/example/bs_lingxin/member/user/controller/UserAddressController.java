package org.example.bs_lingxin.member.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bs_lingxin.common.Result;
import org.example.bs_lingxin.member.user.dto.UserAddressSaveDTO;
import org.example.bs_lingxin.member.user.service.UserAddressService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户收货地址接口
 */
@Slf4j
@RestController
@RequestMapping("/api/user-address")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    /**
     * 获取当前用户地址列表
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(userAddressService.listByUserId(currentUserId()));
    }

    /**
     * 获取用户默认收货地址
     */
    @GetMapping("/default")
    public Result<?> getDefault() {
        return Result.success(userAddressService.selectDefault(currentUserId()));
    }

    /**
     * 根据ID查询单条地址详情
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(userAddressService.selectById(id, currentUserId()));
    }

    /**
     * 新增收货地址
     */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody UserAddressSaveDTO dto) {
        Long userId = currentUserId();
        userAddressService.add(dto, userId);
        log.info("新增收货地址成功，userId={}，receiverName={}", userId, dto.getReceiverName());
        return Result.success();
    }

    /**
     * 修改收货地址
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody UserAddressSaveDTO dto) {
        Long userId = currentUserId();
        userAddressService.update(id, dto, userId);
        log.info("修改收货地址成功，userId={}，id={}", userId, id);
        return Result.success();
    }

    /**
     * 物理删除收货地址
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Long userId = currentUserId();
        userAddressService.delete(id, userId);
        log.info("删除收货地址成功，userId={}，id={}", userId, id);
        return Result.success();
    }

    /**
     * 当前登录用户ID
     */
    private Long currentUserId() {
        return StpUtil.getLoginIdAsLong();
    }
}
