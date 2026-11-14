package org.example.bs_lingxin.member.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.member.user.dto.UserAddressSaveDTO;
import org.example.bs_lingxin.member.user.entity.UserAddress;
import org.example.bs_lingxin.member.user.mapper.UserAddressMapper;
import org.example.bs_lingxin.member.user.service.UserAddressService;
import org.example.bs_lingxin.member.user.vo.UserAddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private static final int IS_DEFAULT_YES = 1;
    private static final int IS_DEFAULT_NO = 0;

    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * 当前用户地址列表，默认地址排在最前，其次按 id 倒序
     */
    @Override
    public List<UserAddressVO> listByUserId(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getId);
        return userAddressMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    /**
     * 单条详情，带归属校验
     */
    @Override
    public UserAddressVO selectById(Long id, Long userId) {
        return toVO(requireOwned(id, userId));
    }

    /**
     * 默认地址
     */
    @Override
    public UserAddressVO selectDefault(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, IS_DEFAULT_YES)
                .orderByDesc(UserAddress::getId)
                // 数据脏了有多条默认时只取最新一条，避免 selectOne 抛 TooManyResultsException
                .last("LIMIT 1");
        UserAddress address = userAddressMapper.selectOne(wrapper);
        if (address == null) {
            throw new BusinessException("默认收货地址不存在", 404);
        }
        return toVO(address);
    }

    /**
     * 新增地址：用户第一条地址自动设为默认
     */
    @Override
    public void add(UserAddressSaveDTO dto, Long userId) {
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(dto, address);
        // 新增忽略前端传来的 id，userId 只认当前登录用户
        address.setId(null);
        address.setUserId(userId);

        Long count = userAddressMapper.selectCount(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId));
        if (count == null || count == 0) {
            // 第一条地址强制默认，否则用户下单时选不到地址
            address.setIsDefault(IS_DEFAULT_YES);
        } else if (address.getIsDefault() == null) {
            address.setIsDefault(IS_DEFAULT_NO);
        }
        if (address.getIsDefault() == IS_DEFAULT_YES) {
            clearDefault(userId);
        }
        if (userAddressMapper.insert(address) < 1) {
            throw new BusinessException("新增收货地址失败，数据库未写入数据");
        }
    }

    /**
     * 修改地址
     */
    @Override
    public void update(Long id, UserAddressSaveDTO dto, Long userId) {
        UserAddress address = requireOwned(id, userId);
        BeanUtils.copyProperties(dto, address);
        // 路径参数优先，userId 不允许被覆盖
        address.setId(id);
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(IS_DEFAULT_NO);
        }
        if (address.getIsDefault() == IS_DEFAULT_YES) {
            clearDefault(userId);
        }
        if (userAddressMapper.updateById(address) == 0) {
            throw new BusinessException("修改收货地址失败，数据库未写入数据");
        }
    }

    /**
     * 物理删除；删掉的是默认地址时，把剩下的最新一条补设为默认
     */
    @Override
    public void delete(Long id, Long userId) {
        UserAddress address = requireOwned(id, userId);
        boolean wasDefault = address.getIsDefault() != null && address.getIsDefault() == IS_DEFAULT_YES;

        if (userAddressMapper.deleteById(id) == 0) {
            throw new BusinessException("删除收货地址失败，数据库未删除数据");
        }
        if (!wasDefault) {
            return;
        }
        UserAddress next = userAddressMapper.selectOne(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getId)
                .last("LIMIT 1"));
        if (next == null) {
            return;
        }
        LambdaUpdateWrapper<UserAddress> wrapper = Wrappers.lambdaUpdate(UserAddress.class)
                .eq(UserAddress::getId, next.getId())
                .set(UserAddress::getIsDefault, IS_DEFAULT_YES);
        userAddressMapper.update(null, wrapper);
    }

    /**
     * 查地址并校验归属：别人的地址一律按「不存在」处理，不暴露数据是否存在
     */
    private UserAddress requireOwned(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new BusinessException("收货地址ID不正确", 400);
        }
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !Objects.equals(address.getUserId(), userId)) {
            throw new BusinessException("收货地址不存在", 404);
        }
        return address;
    }

    /**
     * 把该用户其他地址的 is_default 置 0，保证一人只有一个默认地址
     */
    private void clearDefault(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = Wrappers.lambdaUpdate(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, IS_DEFAULT_NO);
        userAddressMapper.update(null, wrapper);
    }

    private UserAddressVO toVO(UserAddress address) {
        UserAddressVO vo = new UserAddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}
