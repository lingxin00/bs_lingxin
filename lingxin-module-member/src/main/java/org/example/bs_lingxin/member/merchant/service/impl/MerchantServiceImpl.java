package org.example.bs_lingxin.member.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.member.merchant.dto.MerchantLoginDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantPageQueryDTO;
import org.example.bs_lingxin.member.merchant.dto.MerchantProfileDTO;
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
        // password 的 @NotBlank 已去掉（超管改资料时可留空），这里补回新增场景的必填校验
        if (!StringUtils.hasText(merchantSaveDTO.getPassword())){
            throw new BusinessException("密码不能为空", 400);
        }
        // username 是唯一索引，重名直接抛 DuplicateKeyException 会变成 500，先查一次
        if (merchantMapper.countUsernameExcludeId(merchantSaveDTO.getUsername(), -1L) > 0){
            throw new BusinessException("登录账号已存在：" + merchantSaveDTO.getUsername(), 400);
        }
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
     * 商家修改自身资料
     * */
    @Override
    public void updateProfile(Long merchantId, MerchantProfileDTO merchantProfileDTO) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null){
            throw new BusinessException("商家不存在", 404);
        }
        // 直接把 DTO 覆盖到查出来的对象上，password 也会被一起覆盖（null 或空串）
        BeanUtils.copyProperties(merchantProfileDTO, merchant);
        // 有密码才加密；空值统一置 null，updateById 跳过 null 字段，保留数据库里的旧密码
        if (StringUtils.hasText(merchantProfileDTO.getPassword())){
            String encodePwd = passwordEncoder.encode(merchantProfileDTO.getPassword());
            merchant.setPassword(encodePwd);
        }else{
            // 必须置 null：copyProperties 可能把空串 "" 盖进来，空串不是 null，会把密码写成空
            merchant.setPassword(null);
        }
        if (merchantMapper.updateById(merchant) == 0){
            throw new BusinessException("修改商家资料失败，数据库未写入数据");
        }
    }

    /**
     * 超级管理员修改指定商家
     * */
    @Override
    public void updateAdmin(Long merchantId, MerchantSaveDTO merchantSaveDTO) {
        Merchant exist = merchantMapper.selectById(merchantId);
        if (exist == null){
            throw new BusinessException("商家不存在", 404);
        }
        String newUsername = merchantSaveDTO.getUsername();
        if (StringUtils.hasText(newUsername)
                && !newUsername.equals(exist.getUsername())
                && merchantMapper.countUsernameExcludeId(newUsername, merchantId) > 0){
            throw new BusinessException("登录账号已被占用：" + newUsername, 400);
        }
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);
        merchant.setUsername(newUsername);
        merchant.setMerchantName(merchantSaveDTO.getMerchantName());
        merchant.setMerchantCode(merchantSaveDTO.getMerchantCode());
        merchant.setContactName(merchantSaveDTO.getContactName());
        merchant.setContactPhone(merchantSaveDTO.getContactPhone());
        merchant.setContactEmail(merchantSaveDTO.getContactEmail());
        merchant.setAddress(merchantSaveDTO.getAddress());
        merchant.setLogo(merchantSaveDTO.getLogo());
        merchant.setDescription(merchantSaveDTO.getDescription());
        merchant.setStatus(merchantSaveDTO.getStatus());
        // 密码有值才加密更新，留空表示不改密码
        if (StringUtils.hasText(merchantSaveDTO.getPassword())){
            merchant.setPassword(passwordEncoder.encode(merchantSaveDTO.getPassword()));
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
