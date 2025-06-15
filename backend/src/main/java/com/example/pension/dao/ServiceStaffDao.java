package com.example.pension.dao;

import com.example.pension.model.ServiceStaff;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ServiceStaffDao {

    /**
     * 插入服务人员
     */
    @Insert("INSERT INTO service_staff (employee_id, name, gender, birth_date, phone, email, id_card, address, " +
            "position, department, work_type, hire_date, status, salary, work_shift, organization_id, " +
            "education, major, certifications, skills, work_experience, emergency_contact_name, emergency_contact_phone, " +
            "emergency_contact_relationship, health_status, last_physical_exam, medical_history, last_training_date, " +
            "training_records, performance_score, last_evaluation_date, evaluation_comments, created_at, " +
            "updated_at, created_by, updated_by, remarks, avatar, total_work_days, attendance_days, " +
            "leave_days, attendance_rate, total_service_hours, service_count, customer_satisfaction, " +
            "current_shift, next_shift, next_shift_time) VALUES " +
            "(#{employeeId}, #{name}, #{gender}, #{birthDate}, #{phone}, #{email}, #{idCard}, #{address}, " +
            "#{position}, #{department}, #{workType}, #{hireDate}, #{status}, #{salary}, #{workShift}, " +
            "#{organizationId}, #{education}, #{major}, #{certifications}, #{skills}, #{workExperience}, " +
            "#{emergencyContact}, #{emergencyPhone}, #{emergencyRelation}, #{healthStatus}, #{lastPhysicalExam}, " +
            "#{medicalHistory}, #{lastTrainingDate}, #{trainingRecords}, #{performanceScore}, " +
            "#{lastEvaluationDate}, #{evaluationComments}, #{createdAt}, #{updatedAt}, #{createdBy}, " +
            "#{updatedBy}, #{remarks}, #{avatar}, #{totalWorkDays}, #{attendanceDays}, #{leaveDays}, " +
            "#{attendanceRate}, #{totalServiceHours}, #{serviceCount}, #{customerSatisfaction}, " +
            "#{currentShift}, #{nextShift}, #{nextShiftTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ServiceStaff serviceStaff);

    /**
     * 根据ID查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE id = #{id}")
    ServiceStaff findById(@Param("id") Long id);

    /**
     * 根据工号查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE employee_id = #{employeeId}")
    ServiceStaff findByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 多条件查询服务人员
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findWithConditions")
    List<ServiceStaff> findWithConditions(
            @Param("name") String name,
            @Param("employeeId") String employeeId,
            @Param("position") String position,
            @Param("status") String status,
            @Param("organizationId") Long organizationId);

    /**
     * 多条件查询服务人员（扩展版本）
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findWithConditionsExtended")
    List<ServiceStaff> findWithConditionsExtended(
            @Param("name") String name,
            @Param("employeeId") String employeeId,
            @Param("position") String position,
            @Param("department") String department,
            @Param("workType") String workType,
            @Param("workShift") String workShift,
            @Param("status") String status,
            @Param("organizationId") Long organizationId);

    /**
     * 更新服务人员
     */
    @UpdateProvider(type = ServiceStaffSqlProvider.class, method = "updateServiceStaff")
    void update(ServiceStaff serviceStaff);

    /**
     * 删除服务人员
     */
    @Delete("DELETE FROM service_staff WHERE id = #{id}")
    void delete(@Param("id") Long id);

    /**
     * 批量删除服务人员
     */
    @DeleteProvider(type = ServiceStaffSqlProvider.class, method = "batchDelete")
    void batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据机构ID查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE organization_id = #{organizationId} ORDER BY id DESC")
    List<ServiceStaff> findByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据职位查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE position = #{position} ORDER BY id DESC")
    List<ServiceStaff> findByPosition(@Param("position") String position);

    /**
     * 根据状态查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE status = #{status} ORDER BY id DESC")
    List<ServiceStaff> findByStatus(@Param("status") String status);

    /**
     * 根据部门查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE department = #{department} ORDER BY id DESC")
    List<ServiceStaff> findByDepartment(@Param("department") String department);

    /**
     * 根据工作类型查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE work_type = #{workType} ORDER BY id DESC")
    List<ServiceStaff> findByWorkType(@Param("workType") String workType);

    /**
     * 根据班次查找服务人员
     */
    @Select("SELECT * FROM service_staff WHERE work_shift = #{workShift} ORDER BY id DESC")
    List<ServiceStaff> findByWorkShift(@Param("workShift") String workShift);

    /**
     * 根据技能搜索服务人员
     */
    @Select("SELECT * FROM service_staff WHERE skills LIKE CONCAT('%', #{skills}, '%') ORDER BY id DESC")
    List<ServiceStaff> findBySkillsContaining(@Param("skills") String skills);

    /**
     * 统计总数
     */
    @Select("SELECT COUNT(*) FROM service_staff")
    Long countAll();

    /**
     * 根据机构ID统计人数
     */
    @Select("SELECT COUNT(*) FROM service_staff WHERE organization_id = #{organizationId}")
    Long countByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * 根据职位统计人数
     */
    @Select("SELECT COUNT(*) FROM service_staff WHERE position = #{position}")
    Long countByPosition(@Param("position") String position);

    /**
     * 根据状态统计人数
     */
    @Select("SELECT COUNT(*) FROM service_staff WHERE status = #{status}")
    Long countByStatus(@Param("status") String status);

    /**
     * 根据机构ID和状态统计人数
     */
    @Select("SELECT COUNT(*) FROM service_staff WHERE organization_id = #{organizationId} AND status = #{status}")
    Long countByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    /**
     * 查找需要体检的人员
     */
    @Select("SELECT * FROM service_staff WHERE last_physical_exam IS NULL OR last_physical_exam < #{cutoffDate} ORDER BY last_physical_exam ASC")
    List<ServiceStaff> findNeedingPhysicalExam(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 查找需要培训的人员
     */
    @Select("SELECT * FROM service_staff WHERE last_training_date IS NULL OR last_training_date < #{cutoffDate} ORDER BY last_training_date ASC")
    List<ServiceStaff> findNeedingTraining(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 查找即将生日的人员
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findUpcomingBirthdays")
    List<ServiceStaff> findUpcomingBirthdays(@Param("days") int days);

    /**
     * 查找即将工作周年的人员
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findUpcomingWorkAnniversaries")
    List<ServiceStaff> findUpcomingWorkAnniversaries(@Param("days") int days);

    /**
     * 获取职位统计
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "getPositionStatistics")
    @MapKey("position")
    Map<String, Long> getPositionStatistics(@Param("organizationId") Long organizationId);

    /**
     * 获取部门统计
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "getDepartmentStatistics")
    @MapKey("department")
    Map<String, Long> getDepartmentStatistics(@Param("organizationId") Long organizationId);

    /**
     * 获取年龄分布统计
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "getAgeDistribution")
    @MapKey("age_group")
    Map<String, Long> getAgeDistribution(@Param("organizationId") Long organizationId);

    /**
     * 获取学历分布统计
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "getEducationDistribution")
    @MapKey("education")
    Map<String, Long> getEducationDistribution(@Param("organizationId") Long organizationId);

    /**
     * 获取绩效优秀的员工
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findTopPerformers")
    List<ServiceStaff> findTopPerformers(@Param("organizationId") Long organizationId, @Param("limit") int limit);

    /**
     * 获取新员工
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findNewEmployees")
    List<ServiceStaff> findNewEmployees(@Param("organizationId") Long organizationId, @Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 获取资深员工
     */
    @SelectProvider(type = ServiceStaffSqlProvider.class, method = "findSeniorEmployees")
    List<ServiceStaff> findSeniorEmployees(@Param("organizationId") Long organizationId, @Param("cutoffDate") LocalDate cutoffDate);

    /**
     * SQL提供者类
     */
    class ServiceStaffSqlProvider {
        
        public String findWithConditions(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT * FROM service_staff WHERE 1=1");
            
            if (params.get("name") != null) {
                sql.append(" AND name LIKE CONCAT('%', #{name}, '%')");
            }
            if (params.get("employeeId") != null) {
                sql.append(" AND employee_id LIKE CONCAT('%', #{employeeId}, '%')");
            }
            if (params.get("position") != null) {
                sql.append(" AND position = #{position}");
            }
            if (params.get("status") != null) {
                sql.append(" AND status = #{status}");
            }
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            
            sql.append(" ORDER BY id DESC");
            return sql.toString();
        }
        
        public String findWithConditionsExtended(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT * FROM service_staff WHERE 1=1");
            
            if (params.get("name") != null) {
                sql.append(" AND name LIKE CONCAT('%', #{name}, '%')");
            }
            if (params.get("employeeId") != null) {
                sql.append(" AND employee_id LIKE CONCAT('%', #{employeeId}, '%')");
            }
            if (params.get("position") != null) {
                sql.append(" AND position = #{position}");
            }
            if (params.get("department") != null) {
                sql.append(" AND department = #{department}");
            }
            if (params.get("workType") != null) {
                sql.append(" AND work_type = #{workType}");
            }
            if (params.get("workShift") != null) {
                sql.append(" AND work_shift = #{workShift}");
            }
            if (params.get("status") != null) {
                sql.append(" AND status = #{status}");
            }
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            
            sql.append(" ORDER BY id DESC");
            return sql.toString();
        }
        
        public String updateServiceStaff(ServiceStaff serviceStaff) {
            StringBuilder sql = new StringBuilder("UPDATE service_staff SET ");
            sql.append("updated_at = #{updatedAt}");
            
            if (serviceStaff.getEmployeeId() != null) {
                sql.append(", employee_id = #{employeeId}");
            }
            if (serviceStaff.getName() != null) {
                sql.append(", name = #{name}");
            }
            if (serviceStaff.getGender() != null) {
                sql.append(", gender = #{gender}");
            }
            if (serviceStaff.getBirthDate() != null) {
                sql.append(", birth_date = #{birthDate}");
            }
            if (serviceStaff.getPhone() != null) {
                sql.append(", phone = #{phone}");
            }
            if (serviceStaff.getEmail() != null) {
                sql.append(", email = #{email}");
            }
            if (serviceStaff.getIdCard() != null) {
                sql.append(", id_card = #{idCard}");
            }
            if (serviceStaff.getAddress() != null) {
                sql.append(", address = #{address}");
            }
            if (serviceStaff.getPosition() != null) {
                sql.append(", position = #{position}");
            }
            if (serviceStaff.getDepartment() != null) {
                sql.append(", department = #{department}");
            }
            if (serviceStaff.getWorkType() != null) {
                sql.append(", work_type = #{workType}");
            }
            if (serviceStaff.getHireDate() != null) {
                sql.append(", hire_date = #{hireDate}");
            }
            if (serviceStaff.getStatus() != null) {
                sql.append(", status = #{status}");
            }
            if (serviceStaff.getBaseSalary() != null) {
                sql.append(", base_salary = #{baseSalary}");
            }
            if (serviceStaff.getWorkShift() != null) {
                sql.append(", work_shift = #{workShift}");
            }
            if (serviceStaff.getOrganizationId() != null) {
                sql.append(", organization_id = #{organizationId}");
            }
            if (serviceStaff.getEducation() != null) {
                sql.append(", education = #{education}");
            }

            if (serviceStaff.getCertifications() != null) {
                sql.append(", certifications = #{certifications}");
            }
            if (serviceStaff.getSkills() != null) {
                sql.append(", skills = #{skills}");
            }

            if (serviceStaff.getEmergencyContact() != null) {
                sql.append(", emergency_contact_name = #{emergencyContact}");
            }
            if (serviceStaff.getEmergencyPhone() != null) {
                sql.append(", emergency_contact_phone = #{emergencyPhone}");
            }
            if (serviceStaff.getEmergencyRelation() != null) {
                sql.append(", emergency_contact_relationship = #{emergencyRelation}");
            }
            if (serviceStaff.getHealthStatus() != null) {
                sql.append(", health_status = #{healthStatus}");
            }
            if (serviceStaff.getLastPhysicalExamDate() != null) {
                sql.append(", last_physical_exam_date = #{lastPhysicalExamDate}");
            }

            if (serviceStaff.getLastTrainingDate() != null) {
                sql.append(", last_training_date = #{lastTrainingDate}");
            }
            if (serviceStaff.getTrainingRecords() != null) {
                sql.append(", training_records = #{trainingRecords}");
            }
            if (serviceStaff.getPerformanceScore() != null) {
                sql.append(", performance_score = #{performanceScore}");
            }
            if (serviceStaff.getLastEvaluationDate() != null) {
                sql.append(", last_evaluation_date = #{lastEvaluationDate}");
            }

            if (serviceStaff.getUpdatedBy() != null) {
                sql.append(", updated_by = #{updatedBy}");
            }
            if (serviceStaff.getRemarks() != null) {
                sql.append(", remarks = #{remarks}");
            }
            if (serviceStaff.getAvatarUrl() != null) {
                sql.append(", avatar_url = #{avatarUrl}");
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }
        
        public String batchDelete(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) params.get("ids");
            StringBuilder sql = new StringBuilder("DELETE FROM service_staff WHERE id IN (");
            for (int i = 0; i < ids.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("#{ids[").append(i).append("]}");
            }
            sql.append(")");
            return sql.toString();
        }
        
        public String findUpcomingBirthdays(Map<String, Object> params) {
            return "SELECT * FROM service_staff WHERE " +
                   "DATEDIFF(DATE_ADD(birth_date, INTERVAL YEAR(CURDATE()) - YEAR(birth_date) + " +
                   "IF(DAYOFYEAR(CURDATE()) > DAYOFYEAR(birth_date), 1, 0) YEAR), CURDATE()) <= #{days} " +
                   "ORDER BY birth_date";
        }
        
        public String findUpcomingWorkAnniversaries(Map<String, Object> params) {
            return "SELECT * FROM service_staff WHERE " +
                   "DATEDIFF(DATE_ADD(hire_date, INTERVAL YEAR(CURDATE()) - YEAR(hire_date) + " +
                   "IF(DAYOFYEAR(CURDATE()) > DAYOFYEAR(hire_date), 1, 0) YEAR), CURDATE()) <= #{days} " +
                   "ORDER BY hire_date";
        }
        
        public String getPositionStatistics(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT position, COUNT(*) as count FROM service_staff WHERE 1=1");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" GROUP BY position ORDER BY count DESC");
            return sql.toString();
        }
        
        public String getDepartmentStatistics(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT department, COUNT(*) as count FROM service_staff WHERE 1=1");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" GROUP BY department ORDER BY count DESC");
            return sql.toString();
        }
        
        public String getAgeDistribution(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder(
                "SELECT CASE " +
                "WHEN YEAR(CURDATE()) - YEAR(birth_date) < 25 THEN '25岁以下' " +
                "WHEN YEAR(CURDATE()) - YEAR(birth_date) BETWEEN 25 AND 35 THEN '25-35岁' " +
                "WHEN YEAR(CURDATE()) - YEAR(birth_date) BETWEEN 36 AND 45 THEN '36-45岁' " +
                "WHEN YEAR(CURDATE()) - YEAR(birth_date) BETWEEN 46 AND 55 THEN '46-55岁' " +
                "ELSE '55岁以上' END as age_group, COUNT(*) as count " +
                "FROM service_staff WHERE birth_date IS NOT NULL");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" GROUP BY age_group ORDER BY count DESC");
            return sql.toString();
        }
        
        public String getEducationDistribution(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT education, COUNT(*) as count FROM service_staff WHERE education IS NOT NULL");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" GROUP BY education ORDER BY count DESC");
            return sql.toString();
        }
        
        public String findTopPerformers(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT * FROM service_staff WHERE performance_score IS NOT NULL");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" ORDER BY performance_score DESC LIMIT #{limit}");
            return sql.toString();
        }
        
        public String findNewEmployees(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT * FROM service_staff WHERE hire_date >= #{cutoffDate}");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" ORDER BY hire_date DESC");
            return sql.toString();
        }
        
        public String findSeniorEmployees(Map<String, Object> params) {
            StringBuilder sql = new StringBuilder("SELECT * FROM service_staff WHERE hire_date <= #{cutoffDate}");
            if (params.get("organizationId") != null) {
                sql.append(" AND organization_id = #{organizationId}");
            }
            sql.append(" ORDER BY hire_date ASC");
            return sql.toString();
        }
    }
}