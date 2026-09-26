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
    List<Role> selectRolePageOffset(@Param("dto")RolePageQueryDTO rolePageQueryDTO,Long offset);

    /**
     * 查询总条数
     */
    Long selectRoleCount(RolePageQueryDTO rolePageQueryDTO);

    /**
     * 查询单个角色
     * */
    @Select("select * from role where id = #{id}")
    Role selectId(Long id);

    /**
     * 多条件查询单条
     */
    Role selectRoleOneByDto(RolePageQueryDTO rolePageQueryDTO);

    /**
     * 新增角色，注解SQL
     */
    @Insert("INSERT INTO Role(role_name,role_code,description,status) " +
            "VALUES(#{roleName},#{roleCode},#{description},#{status})")
    int insertRole(RoleSaveDTO roleSaveDTO);

    /**
     * 修改角色
     */
    int updateRole(RoleSaveDTO roleSaveDTO);

    /**
     * 逻辑删除：手写SQL 更新 is_deleted=1，不能用mp removeById
     */
    @Update("UPDATE Role SET is_deleted = 1 WHERE id = #{id}")
    int logicDeleteRole(@Param("id") Long id);





}
