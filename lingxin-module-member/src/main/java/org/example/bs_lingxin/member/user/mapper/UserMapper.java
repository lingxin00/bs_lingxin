package org.example.bs_lingxin.member.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.bs_lingxin.member.user.dto.UserPageQueryDTO;
import org.example.bs_lingxin.member.user.entity.User;

import java.util.List;

/**
 * 前端用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * Offset-Limit 分页查询，offset 由 service 计算
     */
    List<User> selectUserPageOffset(@Param("dto") UserPageQueryDTO dto,
                                    @Param("offset") Long offset);

    /**
     * 查询筛选后的总条数，条件与分页查询完全一致
     */
    Long selectUserCount(@Param("dto") UserPageQueryDTO dto);

    /**
     * 校验用户名是否被其他用户占用
     */
    @Select("select count(1) from user where username = #{username} and id != #{excludeId}")
    Long countUsernameExcludeId(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 校验手机号是否被其他用户占用，同样不过滤 is_deleted
     */
    @Select("select count(1) from user where phone = #{phone} and id != #{excludeId}")
    Long countPhoneExcludeId(@Param("phone") String phone, @Param("excludeId") Long excludeId);
}
