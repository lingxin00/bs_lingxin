package org.example.bs_lingxin.mapper;

import org.apache.ibatis.annotations.*;
import org.example.bs_lingxin.dto.AdminPageQueryDTO;
import org.example.bs_lingxin.dto.AdminQueryDTO;
import org.example.bs_lingxin.entity.Admin;

import java.util.List;

@Mapper
public interface AdminMapper {

    /**
     *根据账号密码查询管理员
     * */
    @Select("select * from admin where username = #{username}  and is_deleted = 0")
    Admin selectByUsername(@Param("username") String username);

    /**
     * 查询总条数
     */
    Long selectAdminCount(@Param("dto") AdminPageQueryDTO dto);

    /**
     * Offset-Limit多条件分页查询，offset由service计算
     */
    List<Admin> selectAdminPageOffset(@Param("dto") AdminPageQueryDTO dto, @Param("offset") Long offset);

    /**
     * 查询单个用户
     * */
    @Select("select * from admin where id = #{id}")
    Admin selectId(Long id);

    /**
     * 多条件查询单条
     */
    Admin selectAdminOneByDto(@Param("dto") AdminQueryDTO dto);

    /**
     * 新增管理员，注解SQL
     */
    @Insert("INSERT INTO admin(username,password,real_name,phone,email,avatar,status,role_id) " +
            "VALUES(#{username},#{password},#{realName},#{phone},#{email},#{avatar},#{status},#{roleId})")
    int insertAdmin(Admin admin);

    /**
     * 修改管理员
     */
    int updateAdmin(Admin admin);

    /**
     * 逻辑删除：手写SQL 更新 is_deleted=1，不能用mp removeById
     */
    @Update("UPDATE admin SET is_deleted = 1 WHERE id = #{id}")
    int logicDeleteAdmin(@Param("id") Long id);
}
