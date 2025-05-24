package com.example.pension.mapper;

import com.example.pension.model.SystemUser;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemUserMapper {

    @Select("SELECT su.*, o.name as organization_name " +
            "FROM system_user su " +
            "LEFT JOIN organization o ON su.organization_id = o.id " +
            "WHERE su.id = #{id}")
    SystemUser findById(Long id);

    @Select("SELECT su.*, o.name as organization_name " +
            "FROM system_user su " +
            "LEFT JOIN organization o ON su.organization_id = o.id " +
            "WHERE su.username = #{username}")
    SystemUser findByUsername(String username);

    @Select("SELECT su.*, o.name as organization_name " +
            "FROM system_user su " +
            "LEFT JOIN organization o ON su.organization_id = o.id " +
            "ORDER BY su.create_time DESC")
    List<SystemUser> findAll();

    @Select("<script>" +
            "SELECT su.*, o.name as organization_name " +
            "FROM system_user su " +
            "LEFT JOIN organization o ON su.organization_id = o.id " +
            "WHERE 1=1 " +
            "<if test='username != null and username != \"\"'>" +
            "AND su.username LIKE CONCAT('%', #{username}, '%') " +
            "</if>" +
            "<if test='fullName != null and fullName != \"\"'>" +
            "AND su.full_name LIKE CONCAT('%', #{fullName}, '%') " +
            "</if>" +
            "<if test='email != null and email != \"\"'>" +
            "AND su.email LIKE CONCAT('%', #{email}, '%') " +
            "</if>" +
            "<if test='organizationId != null'>" +
            "AND su.organization_id = #{organizationId} " +
            "</if>" +
            "<if test='isAdmin != null'>" +
            "AND su.is_admin = #{isAdmin} " +
            "</if>" +
            "<if test='isActive != null'>" +
            "AND su.is_active = #{isActive} " +
            "</if>" +
            "ORDER BY su.create_time DESC " +
            "</script>")
    List<SystemUser> findByConditions(@Param("username") String username,
                                      @Param("fullName") String fullName,
                                      @Param("email") String email,
                                      @Param("organizationId") Long organizationId,
                                      @Param("isAdmin") Boolean isAdmin,
                                      @Param("isActive") Boolean isActive);

    @Insert("INSERT INTO system_user (username, password_hash, full_name, email, phone, " +
            "organization_id, is_admin, is_active, create_time, update_time) " +
            "VALUES (#{username}, #{passwordHash}, #{fullName}, #{email}, #{phone}, " +
            "#{organizationId}, #{isAdmin}, #{isActive}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SystemUser systemUser);

    @Update("UPDATE system_user SET " +
            "username = #{username}, " +
            "full_name = #{fullName}, " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "organization_id = #{organizationId}, " +
            "is_admin = #{isAdmin}, " +
            "is_active = #{isActive}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void update(SystemUser systemUser);

    @Update("UPDATE system_user SET password_hash = #{passwordHash}, update_time = NOW() WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Update("UPDATE system_user SET last_login_time = #{lastLoginTime}, update_time = NOW() WHERE id = #{id}")
    void updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    @Delete("DELETE FROM system_user WHERE id = #{id}")
    void deleteById(Long id);

    @Delete("<script>" +
            "DELETE FROM system_user WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchDeleteByIds(@Param("ids") List<Long> ids);

    // 统计方法
    @Select("SELECT COUNT(*) FROM system_user WHERE is_active = true")
    long countActiveUsers();

    @Select("SELECT COUNT(*) FROM system_user WHERE is_admin = true")
    long countAdminUsers();

    @Select("SELECT COUNT(*) FROM system_user WHERE organization_id = #{organizationId}")
    long countByOrganizationId(Long organizationId);
} 