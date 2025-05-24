package com.example.pension.mapper;

import com.example.pension.model.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT * FROM role WHERE id = #{id}")
    Role findById(Long id);

    @Select("SELECT * FROM role ORDER BY create_time DESC")
    List<Role> findAll();

    @Select("<script>" +
            "SELECT * FROM role WHERE 1=1 " +
            "<if test='roleName != null and roleName != \"\"'>" +
            "AND role_name LIKE CONCAT('%', #{roleName}, '%') " +
            "</if>" +
            "<if test='roleKey != null and roleKey != \"\"'>" +
            "AND role_key LIKE CONCAT('%', #{roleKey}, '%') " +
            "</if>" +
            "ORDER BY create_time DESC " +
            "</script>")
    List<Role> findByConditions(@Param("roleName") String roleName,
                                @Param("roleKey") String roleKey);

    @Insert("INSERT INTO role (role_name, role_key, description, create_time, update_time) " +
            "VALUES (#{roleName}, #{roleKey}, #{description}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Role role);

    @Update("UPDATE role SET " +
            "role_name = #{roleName}, " +
            "role_key = #{roleKey}, " +
            "description = #{description}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void update(Role role);

    @Delete("DELETE FROM role WHERE id = #{id}")
    void deleteById(Long id);

    @Delete("<script>" +
            "DELETE FROM role WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchDeleteByIds(@Param("ids") List<Long> ids);

    // 根据用户ID查询角色
    @Select("SELECT r.* FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> findByUserId(Long userId);

    // 检查角色键是否存在
    @Select("SELECT COUNT(*) FROM role WHERE role_key = #{roleKey} AND id != #{excludeId}")
    int countByRoleKeyExcludeId(@Param("roleKey") String roleKey, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM role WHERE role_key = #{roleKey}")
    int countByRoleKey(String roleKey);
} 