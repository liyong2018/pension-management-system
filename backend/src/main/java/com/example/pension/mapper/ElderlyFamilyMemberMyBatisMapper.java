package com.example.pension.mapper;

import com.example.pension.model.ElderlyFamilyMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElderlyFamilyMemberMyBatisMapper {

    @Select("SELECT * FROM elderly_family_member WHERE id = #{id}")
    ElderlyFamilyMember findById(Long id);

    @Select("SELECT * FROM elderly_family_member WHERE elderly_id = #{elderlyId}")
    List<ElderlyFamilyMember> findAllByElderlyId(Long elderlyId);

    @Insert({
        "INSERT INTO elderly_family_member (elderly_id, name, relationship, gender, age, phone, ",
        "address, occupation, is_emergency_contact, notes) VALUES ",
        "(#{elderlyId}, #{name}, #{relationship}, #{gender}, #{age}, #{phone}, ",
        "#{address}, #{occupation}, #{isEmergencyContact}, #{notes})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ElderlyFamilyMember familyMember);

    @Update({
        "<script>",
        "UPDATE elderly_family_member SET",
        "<if test='name != null'>name = #{name},</if>",
        "<if test='relationship != null'>relationship = #{relationship},</if>",
        "<if test='gender != null'>gender = #{gender},</if>",
        "<if test='age != null'>age = #{age},</if>",
        "<if test='phone != null'>phone = #{phone},</if>",
        "<if test='address != null'>address = #{address},</if>",
        "<if test='occupation != null'>occupation = #{occupation},</if>",
        "<if test='isEmergencyContact != null'>is_emergency_contact = #{isEmergencyContact},</if>",
        "<if test='notes != null'>notes = #{notes},</if>",
        "update_time = CURRENT_TIMESTAMP",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(ElderlyFamilyMember familyMember);

    @Delete("DELETE FROM elderly_family_member WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM elderly_family_member WHERE elderly_id = #{elderlyId}")
    int deleteByElderlyId(Long elderlyId);
} 