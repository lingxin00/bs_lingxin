package org.example.bs_lingxin.member.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;

import java.util.List;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * Offset-Limit多条件分页查询，offset由service计算
     * @Param 必须写，否则 XML 里的 #{dto.xxx} 取不到值
     */
    List<Merchant> selectMerchantPageOffset(@Param("dto") MerchantPageQueryDTO dto,
                                            @Param("offset") Long offset);

    /**
     * 查询筛选后的总条数，条件与分页查询完全一致
     */
    Long selectMerchantCount(@Param("dto") MerchantPageQueryDTO dto);
}
