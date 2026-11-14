package org.example.bs_lingxin.member.user.service;

import org.example.bs_lingxin.member.user.dto.UserAddressSaveDTO;
import org.example.bs_lingxin.member.user.vo.UserAddressVO;

import java.util.List;

/**
 * 用户收货地址服务
 */
public interface UserAddressService {

    /**
     * 查询当前用户全部地址（默认地址排最前）
     */
    List<UserAddressVO> listByUserId(Long userId);

    /**
     * 查询单条地址，带归属校验
     */
    UserAddressVO selectById(Long id, Long userId);

    /**
     * 查询当前用户的默认地址
     */
    UserAddressVO selectDefault(Long userId);

    /**
     * 新增地址
     */
    void add(UserAddressSaveDTO dto, Long userId);

    /**
     * 修改地址，id 以路径为准
     */
    void update(Long id, UserAddressSaveDTO dto, Long userId);

    /**
     * 物理删除地址（表无 is_deleted）
     */
    void delete(Long id, Long userId);
}
