package com.example.pension.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RolePermissionMapper {

    @Insert("INSERT INTO role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    void insert(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Insert("<script>" +
            "INSERT INTO role_permission (role_id, permission_id) VALUES " +
            "<foreach collection='permissionIds' item='permissionId' separator=','>" +
            "(#{roleId}, #{permissionId})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Delete("DELETE FROM role_permission WHERE permission_id = #{permissionId}")
    void deleteByPermissionId(Long permissionId);

    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    void delete(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Select("SELECT permission_id FROM role_permission WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(Long roleId);

    @Select("SELECT role_id FROM role_permission WHERE permission_id = #{permissionId}")
    List<Long> findRoleIdsByPermissionId(Long permissionId);

    @Select("SELECT COUNT(*) FROM role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int countByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
} 