package com.example.pension.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    @Insert("INSERT INTO user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Insert("<script>" +
            "INSERT INTO user_role (user_id, role_id) VALUES " +
            "<foreach collection='roleIds' item='roleId' separator=','>" +
            "(#{userId}, #{roleId})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    @Delete("DELETE FROM user_role WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    void delete(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(Long userId);

    @Select("SELECT user_id FROM user_role WHERE role_id = #{roleId}")
    List<Long> findUserIdsByRoleId(Long roleId);

    @Select("SELECT COUNT(*) FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
} 