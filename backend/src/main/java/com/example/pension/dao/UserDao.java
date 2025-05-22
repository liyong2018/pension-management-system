package com.example.pension.dao;

import com.example.pension.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserDao {
    Optional<User> findByUsername(@Param("username") String username);
    boolean existsByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    long save(User user); // 返回影响的行数或者主键 (如果配置了 useGeneratedKeys)
    int update(User user);
    int deleteById(@Param("id") Long id);
} 