package com.example.pension.mapper;

import com.example.pension.model.Volunteer;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface VolunteerMapper {

    @Select("SELECT * FROM volunteer WHERE id = #{id}")
    Volunteer findById(Long id);

    @Select({
        "<script>",
        "SELECT * FROM volunteer WHERE 1=1",
        "<if test='name != null and name != \"\"'>",
        "AND name LIKE CONCAT('%', #{name}, '%')",
        "</if>",
        "<if test='phone != null and phone != \"\"'>",
        "AND phone LIKE CONCAT('%', #{phone}, '%')",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND status = #{status}",
        "</if>",
        "ORDER BY create_time DESC",
        "LIMIT #{offset}, #{limit}",
        "</script>"
    })
    List<Volunteer> findByConditions(@Param("name") String name, 
                                   @Param("phone") String phone, 
                                   @Param("status") String status,
                                   @Param("offset") int offset, 
                                   @Param("limit") int limit);

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM volunteer WHERE 1=1",
        "<if test='name != null and name != \"\"'>",
        "AND name LIKE CONCAT('%', #{name}, '%')",
        "</if>",
        "<if test='phone != null and phone != \"\"'>",
        "AND phone LIKE CONCAT('%', #{phone}, '%')",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND status = #{status}",
        "</if>",
        "</script>"
    })
    int countByConditions(@Param("name") String name, 
                         @Param("phone") String phone, 
                         @Param("status") String status);

    @Insert({
        "INSERT INTO volunteer (name, gender, age, id_card_number, phone, email, address_detail, ",
        "household_address, education, graduation_school, occupation, work_unit, professional_skills, ",
        "hobbies, language_ability, service_intention, available_time, service_experience, ",
        "emergency_contact_name, emergency_contact_relationship, emergency_contact_phone, ",
        "registration_date, status, total_service_hours, points) VALUES ",
        "(#{name}, #{gender}, #{age}, #{idCardNumber}, #{phone}, #{email}, #{addressDetail}, ",
        "#{householdAddress}, #{education}, #{graduationSchool}, #{occupation}, #{workUnit}, #{professionalSkills}, ",
        "#{hobbies}, #{languageAbility}, #{serviceIntention}, #{availableTime}, #{serviceExperience}, ",
        "#{emergencyContactName}, #{emergencyContactRelationship}, #{emergencyContactPhone}, ",
        "#{registrationDate}, #{status}, #{totalServiceHours}, #{points})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Volunteer volunteer);

    @Update({
        "<script>",
        "UPDATE volunteer SET",
        "<if test='name != null'>name = #{name},</if>",
        "<if test='gender != null'>gender = #{gender},</if>",
        "<if test='age != null'>age = #{age},</if>",
        "<if test='idCardNumber != null'>id_card_number = #{idCardNumber},</if>",
        "<if test='phone != null'>phone = #{phone},</if>",
        "<if test='email != null'>email = #{email},</if>",
        "<if test='addressDetail != null'>address_detail = #{addressDetail},</if>",
        "<if test='householdAddress != null'>household_address = #{householdAddress},</if>",
        "<if test='education != null'>education = #{education},</if>",
        "<if test='graduationSchool != null'>graduation_school = #{graduationSchool},</if>",
        "<if test='occupation != null'>occupation = #{occupation},</if>",
        "<if test='workUnit != null'>work_unit = #{workUnit},</if>",
        "<if test='professionalSkills != null'>professional_skills = #{professionalSkills},</if>",
        "<if test='hobbies != null'>hobbies = #{hobbies},</if>",
        "<if test='languageAbility != null'>language_ability = #{languageAbility},</if>",
        "<if test='serviceIntention != null'>service_intention = #{serviceIntention},</if>",
        "<if test='availableTime != null'>available_time = #{availableTime},</if>",
        "<if test='serviceExperience != null'>service_experience = #{serviceExperience},</if>",
        "<if test='emergencyContactName != null'>emergency_contact_name = #{emergencyContactName},</if>",
        "<if test='emergencyContactRelationship != null'>emergency_contact_relationship = #{emergencyContactRelationship},</if>",
        "<if test='emergencyContactPhone != null'>emergency_contact_phone = #{emergencyContactPhone},</if>",
        "<if test='registrationDate != null'>registration_date = #{registrationDate},</if>",
        "<if test='status != null'>status = #{status},</if>",
        "<if test='totalServiceHours != null'>total_service_hours = #{totalServiceHours},</if>",
        "<if test='points != null'>points = #{points},</if>",
        "update_time = CURRENT_TIMESTAMP",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(Volunteer volunteer);

    @Delete("DELETE FROM volunteer WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE volunteer SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE volunteer SET total_service_hours = total_service_hours + #{hours}, " +
            "points = points + #{points} WHERE id = #{id}")
    int updateServiceStats(@Param("id") Long id, 
                          @Param("hours") Double hours, 
                          @Param("points") Integer points);

    @Select("SELECT * FROM volunteer WHERE status = 'ACTIVE' ORDER BY total_service_hours DESC LIMIT 10")
    List<Volunteer> findTopVolunteers();

    @Select("SELECT * FROM volunteer WHERE phone = #{phone}")
    Volunteer findByPhone(String phone);

    @Select("SELECT * FROM volunteer WHERE id_card_number = #{idCardNumber}")
    Volunteer findByIdCardNumber(String idCardNumber);

    // 计算志愿者的服务时长总和（从服务记录表）
    @Select("SELECT COALESCE(SUM(service_duration), 0) FROM service_record " +
            "WHERE service_provider_type = '志愿者' AND service_provider_id = #{volunteerId}")
    BigDecimal calculateTotalServiceHours(@Param("volunteerId") Long volunteerId);

    // 计算志愿者的积分总和（从服务记录表的工单价格）
    @Select("SELECT COALESCE(SUM(work_order_price), 0) FROM service_record " +
            "WHERE service_provider_type = '志愿者' AND service_provider_id = #{volunteerId}")
    BigDecimal calculateTotalPoints(@Param("volunteerId") Long volunteerId);

    // 更新志愿者的服务时长和积分（基于服务记录计算）
    @Update("UPDATE volunteer SET " +
            "total_service_hours = (SELECT COALESCE(SUM(service_duration), 0) FROM service_record " +
            "WHERE service_provider_type = '志愿者' AND service_provider_id = #{volunteerId}), " +
            "points = (SELECT COALESCE(SUM(work_order_price), 0) FROM service_record " +
            "WHERE service_provider_type = '志愿者' AND service_provider_id = #{volunteerId}) " +
            "WHERE id = #{volunteerId}")
    int updateServiceStatsFromRecords(@Param("volunteerId") Long volunteerId);
} 