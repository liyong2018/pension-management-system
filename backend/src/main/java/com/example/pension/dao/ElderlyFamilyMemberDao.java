package com.example.pension.dao;

import com.example.pension.model.ElderlyFamilyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElderlyFamilyMemberDao {
    ElderlyFamilyMember findById(@Param("id") Long id);

    List<ElderlyFamilyMember> findAllByElderlyId(@Param("elderlyId") Long elderlyId);

    int insert(ElderlyFamilyMember elderlyFamilyMember);

    int update(ElderlyFamilyMember elderlyFamilyMember);

    int deleteById(@Param("id") Long id);

    int deleteByElderlyId(@Param("elderlyId") Long elderlyId);
} 