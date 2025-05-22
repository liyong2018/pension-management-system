package com.example.pension.dao;

import com.example.pension.model.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrganizationDao {
    Optional<Organization> findByName(@Param("name") String name);
    Organization findById(@Param("id") Long id);
    List<Organization> findAll(); // 新增：查询所有
    List<Organization> findByNameContaining(@Param("name") String name);
    long save(Organization organization); // 返回影响的行数或者主键
    int update(Organization organization);
    int deleteById(@Param("id") Long id);
    int deleteByIds(@Param("ids") List<Long> ids);
} 