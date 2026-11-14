package org.example.bs_lingxin.member.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.member.user.dto.UserLoginDTO;
import org.example.bs_lingxin.member.user.dto.UserPageQueryDTO;
import org.example.bs_lingxin.member.user.dto.UserProfileDTO;
import org.example.bs_lingxin.member.user.dto.UserRegisterDTO;
import org.example.bs_lingxin.member.user.dto.UserStatusDTO;
import org.example.bs_lingxin.member.user.dto.UserUpdateDTO;
import org.example.bs_lingxin.member.user.entity.User;
import org.example.bs_lingxin.member.user.mapper.UserMapper;
import org.example.bs_lingxin.member.user.service.UserService;
import org.example.bs_lingxin.member.user.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    /** 限制最大跳转页码，防止深分页拖垮数据库 */
    private static final long MAX_ALLOW_PAGE = 50;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_BANNED = 0;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 手机号 + 密码登录
     *
     * 流程：查手机号 → 判存在 → 判封禁 → 校验密码 → 刷新最后登录时间/IP
     */
    @Override
    public User login(UserLoginDTO dto, String loginIp) {
        // @TableLogic 已标注 isDeleted，MP 会自动补 is_deleted = 0，无需手写
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getPhone, dto.getPhone());
        User user = userMapper.selectOne(wrapper);
        // 账号不存在和密码错误返回同一句提示，避免被撞库探测出哪些手机号注册过
        if (user == null) {
            throw new BusinessException("账号或密码错误", 401);
        }
        if (user.getStatus() != null && user.getStatus() == STATUS_BANNED) {
            throw new BusinessException("账号已被封禁", 403);
        }
        boolean ok = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!ok) {
            throw new BusinessException("账号或密码错误", 400);
        }

        // 只更新两个登录字段：updateById 只写非 null 列，不会覆盖其它字段
        LocalDateTime now = LocalDateTime.now();
        User update = new User();
        update.setId(user.getId());
        update.setLastLoginTime(now);
        update.setLastLoginIp(loginIp);
        userMapper.updateById(update);

        // 回填给 controller，登录返回里能带上最新登录时间
        user.setLastLoginTime(now);
        user.setLastLoginIp(loginIp);
        return user;
    }

    /**
     * 用户注册：用户名、手机号都要唯一，密码 BCrypt 加密后入库
     */
    @Override
    public void register(UserRegisterDTO dto) {
        Long sameUsername = userMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, dto.getUsername()));
        if (sameUsername != null && sameUsername > 0) {
            throw new BusinessException("用户名已存在", 400);
        }
        Long samePhone = userMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getPhone, dto.getPhone()));
        if (samePhone != null && samePhone > 0) {
            throw new BusinessException("手机号已注册", 400);
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // 昵称留空时用用户名兜底，前端不至于显示空白
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(dto.getUsername());
        }
        user.setStatus(STATUS_NORMAL);
        // 显式给 0，不完全依赖数据库默认值；否则 is_deleted 为 NULL 时查询会漏掉这条
        user.setIsDeleted(0);
        if (userMapper.insert(user) < 1) {
            throw new BusinessException("注册失败，数据库未写入数据");
        }
    }

    /**
     * 后台分页查询
     */
    @Override
    public List<User> selectPage(UserPageQueryDTO dto) {
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
        // XML 的公共列片段不含 password，所以查出来的实体 password 为 null
        return userMapper.selectUserPageOffset(dto, offset);
    }

    /**
     * 按ID查询用户
     * 必须转 VO：selectById 是 MP 内置方法，会把 password 哈希一起查出来
     */
    @Override
    public UserVO selectId(Long id) {
        return new UserVO(requireUser(id));
    }

    /**
     * 当前登录用户个人信息
     */
    @Override
    public UserVO info(Long userId) {
        return new UserVO(requireUser(userId));
    }

    /**
     * 修改用户状态（1正常 0封禁）
     */
    @Override
    public void updateStatus(Long id, UserStatusDTO dto) {
        if (dto.getStatus() == null) {
            throw new BusinessException("状态不能为空", 400);
        }
        if (dto.getStatus() != STATUS_NORMAL && dto.getStatus() != STATUS_BANNED) {
            throw new BusinessException("状态只能是 1（正常）或 0（封禁）", 400);
        }
        requireUser(id);

        User update = new User();
        update.setId(id);
        update.setStatus(dto.getStatus());
        if (userMapper.updateById(update) == 0) {
            throw new BusinessException("修改用户状态失败，数据库未写入数据");
        }
    }

    /**
     * 用户自助修改资料：只允许改昵称、头像、邮箱三个字段
     */
    @Override
    public void updateProfile(Long userId, UserProfileDTO dto) {
        requireUser(userId);

        if (StringUtils.hasText(dto.getEmail())) {
            Long sameEmail = userMapper.selectCount(Wrappers.lambdaQuery(User.class)
                    .eq(User::getEmail, dto.getEmail())
                    .ne(User::getId, userId));
            if (sameEmail != null && sameEmail > 0) {
                throw new BusinessException("邮箱已被占用", 400);
            }
        }

        // 用 UpdateWrapper 显式指定列，不用 updateById 整行回写，避免误改状态、密码等字段
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getId, userId)
                .set(dto.getNickname() != null, User::getNickname, dto.getNickname())
                .set(dto.getAvatar() != null, User::getAvatar, dto.getAvatar())
                .set(dto.getEmail() != null, User::getEmail, dto.getEmail())
                // entity 为 null 时自动填充不生效，这里手动刷更新时间
                .set(User::getUpdateTime, LocalDateTime.now());
        if (userMapper.update(null, wrapper) == 0) {
            throw new BusinessException("修改个人资料失败，数据库未写入数据");
        }
    }

    /**
     * 超管修改指定用户
     *
     * 目标 id 来自路径，可以改别人，并且放开 username / phone / status / password。
     * 用 UpdateWrapper 逐列显式 set：为 null 的字段直接跳过，等于「不传不改」，
     * 避免 updateById 整行回写时把没传的字段写成 null。
     */
    @Override
    public void updateByAdmin(Long id, UserUpdateDTO dto) {
        requireUser(id);

        // username / phone 是唯一索引，改成别人占用（含已逻辑删除）的值会抛 DuplicateKeyException，
        // 提前查一次换成可读提示。条件直接用字符串拼的 SQL 查的是库里的真实全量数据
        if (StringUtils.hasText(dto.getUsername())
                && userMapper.countUsernameExcludeId(dto.getUsername(), id) > 0) {
            throw new BusinessException("用户名已被占用：" + dto.getUsername(), 400);
        }
        if (StringUtils.hasText(dto.getPhone())
                && userMapper.countPhoneExcludeId(dto.getPhone(), id) > 0) {
            throw new BusinessException("手机号已被注册：" + dto.getPhone(), 400);
        }
        if (dto.getStatus() != null
                && dto.getStatus() != STATUS_NORMAL && dto.getStatus() != STATUS_BANNED) {
            throw new BusinessException("状态只能是 1（正常）或 0（封禁）", 400);
        }

        boolean hasPassword = StringUtils.hasText(dto.getPassword());
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getId, id)
                // username / phone 是 NOT NULL 唯一列，不接受空串，只接受有值时才改
                .set(StringUtils.hasText(dto.getUsername()), User::getUsername, dto.getUsername())
                .set(StringUtils.hasText(dto.getPhone()), User::getPhone, dto.getPhone())
                .set(dto.getEmail() != null, User::getEmail, dto.getEmail())
                .set(dto.getAvatar() != null, User::getAvatar, dto.getAvatar())
                .set(dto.getNickname() != null, User::getNickname, dto.getNickname())
                .set(dto.getStatus() != null, User::getStatus, dto.getStatus())
                // 密码有值才加密更新，留空表示不改密码
                .set(hasPassword, User::getPassword,
                        hasPassword ? passwordEncoder.encode(dto.getPassword()) : null)
                // entity 传 null 时自动填充不生效，手动刷更新时间
                .set(User::getUpdateTime, LocalDateTime.now());
        if (userMapper.update(null, wrapper) == 0) {
            throw new BusinessException("修改用户失败，数据库未写入数据");
        }
    }

    /**
     * 逻辑删除：User 有 @TableLogic，deleteById 会转成 UPDATE ... SET is_deleted = 1
     */
    @Override
    public void delete(Long id) {
        requireUser(id);
        if (userMapper.deleteById(id) == 0) {
            throw new BusinessException("删除用户失败，数据库未删除数据");
        }
    }

    /**
     * 查用户并校验存在，不存在统一抛 404
     */
    private User requireUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不正确", 400);
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在", 404);
        }
        return user;
    }
}
