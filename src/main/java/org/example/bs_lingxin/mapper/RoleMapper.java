package org.example.bs_lingxin.mapper;

import org.apache.ibatis.annotations.*;
import org.example.bs_lingxin.dto.RolePageQueryDTO;
import org.example.bs_lingxin.dto.RoleSaveDTO;
import org.example.bs_lingxin.entity.Role;
import java.util.List;

@Mapper
public interface RoleMapper {

    /**
     * Offset-Limit多条件分页查询，offset由service计算
     */
    List<Role> selectRolePageOffset(@Param("dto") RolePageQueryDTO rolePageQueryDTO,Long offset);

    /**
     * 查询总条数
     */
    Long selectRoleCount(@Param("dto") RolePageQueryDTO rolePageQueryDTO);

    /**
     * 查询单个角色
     * */
    @Select("select * from role where id = #{id}")
    Role selectId(Long id);

    /**
     * 多条件查询单条
     */
    Role selectRoleOneByDto(@Param("dto") RolePageQueryDTO rolePageQueryDTO);

    /**
     * 新增角色，注解SQL
     */
    @Insert("INSERT INTO role(role_name, role_code, description, status, is_deleted, create_time, update_time) " +
            "VALUES(#{roleName}, #{roleCode}, #{description}, #{status}, 0, NOW(), NOW())")
    int insertRole(RoleSaveDTO roleSaveDTO);

    /**
     * 修改角色
     */
    int updateRole(Role role);

    /**
     * 逻辑删除：手写SQL 更新 is_deleted=1，不能用mp removeById
     */
    @Update("UPDATE role SET is_deleted = 1, update_time = NOW() WHERE id = #{id} AND is_deleted = 0")
    int logicDeleteRole(@Param("id") Long id);





}
