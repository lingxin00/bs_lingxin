package org.example.bs_lingxin.member.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.member.merchant.dto.MerchantLoginDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantSaveDTO;
import org.example.bs_lingxin.member.merchant.entity.Merchant;
import org.example.bs_lingxin.member.merchant.mapper.MerchantMapper;
import org.example.bs_lingxin.member.merchant.service.MerchantService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final PasswordEncoder passwordEncoder;
    private final MerchantMapper merchantMapper;

    public MerchantServiceImpl(PasswordEncoder passwordEncoder, MerchantMapper merchantMapper) {
        this.passwordEncoder = passwordEncoder;
        this.merchantMapper = merchantMapper;
    }

    /**
     * 登录，校验解密密码
     * */
    @Override
    public Merchant login(MerchantLoginDTO merchantLoginDTO) {
        // lambdaQuery 必须传实体类，它靠 Merchant 上的 @TableName 反射出表名和列名
        // eq 的第二个参数必须是「实体」的方法引用，第三个参数才是值
        LambdaQueryWrapper<Merchant> wrapper = Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getUsername, merchantLoginDTO.getUsername());
        // @TableLogic 已标注 isDeleted，MP 会自动补 is_deleted = 0，无需手写
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if (merchant == null) {
            throw new BusinessException("账号不存在", 401);
        }
        boolean ok = passwordEncoder.matches(merchantLoginDTO.getPassword(), merchant.getPassword());
        if (!ok) {
            throw new BusinessException("密码不匹配", 400);
        }
        return merchant;
    }

    /**
     * 分页查询商家
     * */
    @Override
    public List<Merchant> selectpage(MerchantPageQueryDTO merchantPageQueryDTO) {
        // 限制最大跳转页码50页
        final long MAX_ALLOW_PAGE = 50;
        Long current = merchantPageQueryDTO.getCurrent();
        Long size = merchantPageQueryDTO.getSize();
        if (current == null || current < 1) {
            throw new BusinessException("页码不能小于1");
        }
        if (size == null || size < 1) {
            throw new BusinessException("每页条数不能小于1");
        }
        if (current > MAX_ALLOW_PAGE) {
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集");
        }
        Long offset = (current-1) * size;
        List<Merchant> merchants = merchantMapper.selectMerchantPageOffset(merchantPageQueryDTO,offset);
        if (merchants == null){
            throw new BusinessException("商家数据为空");
        }
        return merchants;
    }

    /**
     * 根据ID查询商家
     * */
    @Override
    public Merchant selectId(Long id) {
        LambdaQueryWrapper<Merchant> wrapper = Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getId,id);
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if (merchant == null){
            throw new BusinessException("账号不存在",401);
        }
        return merchant;
    }

    /**
     *根据用户名精确查询商家
     * */
    @Override
    public Merchant selectName(MerchantPageQueryDTO merchantPageQueryDTO) {
        LambdaQueryWrapper<Merchant> wrapper = Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getMerchantName,merchantPageQueryDTO.getMerchantName());
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if (merchant == null){
            throw new BusinessException("账号不存在",401);
        }
        return merchant;
    }

    /**
     * 新增商家
     * */
    @Override
    public void add(MerchantSaveDTO merchantSaveDTO) {
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantSaveDTO,merchant);
        String encodePwd = passwordEncoder.encode(merchantSaveDTO.getPassword());
        merchant.setPassword(encodePwd);
        int insert = merchantMapper.insert(merchant);
        if (insert < 1){
            throw new BusinessException("新增商家失败，数据库未写入数据");
        }
    }

    /**
     * 修改商家
     * */
    @Override
    public void update(MerchantSaveDTO merchantSaveDTO) {
        Merchant merchant = merchantMapper.selectById(merchantSaveDTO.getId());
        if (merchant == null){
            throw new BusinessException("商家不存在", 400);
        }
        // 直接把 DTO 覆盖到查出来的对象上，password 也会被一起覆盖（null 或空串）
        BeanUtils.copyProperties(merchantSaveDTO, merchant);
        // 有密码才加密；空值统一置 null，updateById 跳过 null 字段，保留数据库里的旧密码
        if (StringUtils.hasText(merchantSaveDTO.getPassword())){
            String encodePwd = passwordEncoder.encode(merchantSaveDTO.getPassword());
            merchant.setPassword(encodePwd);
        }else{
            // 必须置 null：copyProperties 可能把空串 "" 盖进来，空串不是 null，会把密码写成空
            merchant.setPassword(null);
        }
        if (merchantMapper.updateById(merchant) == 0){
            throw new BusinessException("修改商家失败，数据库未写入数据");
        }
    }

    /**
     *逻辑删除商家
     * */
    @Override
    public void delect(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null){
            throw new BusinessException("商家不存在", 400);
        }
        if (merchantMapper.deleteById(id) == 0){
            throw new BusinessException("商家删除失败");
        }
    }
}
