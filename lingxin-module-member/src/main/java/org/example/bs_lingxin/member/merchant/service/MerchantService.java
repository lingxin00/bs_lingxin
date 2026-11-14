package org.example.bs_lingxin.member.merchant.service;

import org.example.bs_lingxin.member.merchant.dto.MerchantLoginDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantSaveDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;

import java.util.List;

public interface MerchantService {

    Merchant login(MerchantLoginDTO merchantLoginDTO);

    List<Merchant> selectpage(MerchantPageQueryDTO merchantPageQueryDTO);

    Merchant selectId(Long id);

    Merchant selectName(MerchantPageQueryDTO merchantPageQueryDTO);

    void add(MerchantSaveDTO merchantSaveDTO);

    void update(MerchantSaveDTO merchantSaveDTO);

    void delect(Long id);
}
