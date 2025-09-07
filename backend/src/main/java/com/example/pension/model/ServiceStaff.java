package com.example.pension.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 服务人员实体类
 * 用于管理养老院的服务人员信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
public class ServiceStaff {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 员工编号（唯一）
     */
    @NotBlank(message = "员工编号不能为空")
    @Size(max = 50, message = "员工编号长度不能超过50个字符")
    private String employeeId;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;
    
    /**
     * 性别（男、女）
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(男|女)$", message = "性别只能是男或女")
    private String gender;
    
    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    @Past(message = "出生日期必须是过去的日期")
    private LocalDate birthDate;
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String idCard;
    
    /**
     * 地址
     */
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
    
    /**
     * 职位
     */
    @NotBlank(message = "职位不能为空")
    @Size(max = 100, message = "职位长度不能超过100个字符")
    private String position;
    
    /**
     * 部门
     */
    @Size(max = 100, message = "部门长度不能超过100个字符")
    private String department;
    
    /**
     * 工作类型（FULL_TIME-全职，PART_TIME-兼职，CONTRACT-合同工）
     */
    @NotBlank(message = "工作类型不能为空")
    @Pattern(regexp = "^(FULL_TIME|PART_TIME|CONTRACT)$", message = "工作类型只能是FULL_TIME、PART_TIME或CONTRACT")
    private String workType;
    
    /**
     * 入职日期
     */
    @NotNull(message = "入职日期不能为空")
    private LocalDate hireDate;
    
    /**
     * 状态（ACTIVE-在职，INACTIVE-离职，SUSPENDED-停职）
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "状态只能是ACTIVE、INACTIVE或SUSPENDED")
    private String status;
    
    /**
     * 基本工资
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "基本工资必须大于0")
    @Digits(integer = 10, fraction = 2, message = "基本工资格式不正确")
    private BigDecimal baseSalary;
    
    /**
     * 工作班次（DAY-白班，NIGHT-夜班，ROTATING-轮班）
     */
    @Pattern(regexp = "^(DAY|NIGHT|ROTATING)$", message = "工作班次只能是DAY、NIGHT或ROTATING")
    private String workShift;
    
    /**
     * 所属机构ID
     */
    @NotNull(message = "所属机构不能为空")
    private Long organizationId;
    
    /**
     * 教育程度
     */
    @Size(max = 50, message = "教育程度长度不能超过50个字符")
    private String education;
    
    /**
     * 专业技能（JSON格式存储）
     */
    private String skills;
    
    /**
     * 证书信息（JSON格式存储）
     */
    private String certifications;
    

    
    /**
     * 紧急联系人姓名
     */
    @Size(max = 100, message = "紧急联系人姓名长度不能超过100个字符")
    private String emergencyContact;
    
    /**
     * 紧急联系人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    private String emergencyPhone;
    
    /**
     * 紧急联系人关系
     */
    @Size(max = 50, message = "紧急联系人关系长度不能超过50个字符")
    private String emergencyRelation;
    
    /**
     * 健康状况
     */
    @Size(max = 200, message = "健康状况长度不能超过200个字符")
    private String healthStatus;
    
    /**
     * 上次体检日期
     */
    private LocalDate lastPhysicalExamDate;
    
    /**
     * 下次体检日期
     */
    private LocalDate nextPhysicalExamDate;
    
    /**
     * 上次培训日期
     */
    private LocalDate lastTrainingDate;
    
    /**
     * 培训记录（JSON格式存储）
     */
    private String trainingRecords;
    
    /**
     * 下次培训日期
     */
    private LocalDate nextTrainingDate;

    /**
     * 绩效评分
     */
    @DecimalMin(value = "0.0", message = "绩效评分不能小于0")
    @DecimalMax(value = "100.0", message = "绩效评分不能大于100")
    @Digits(integer = 3, fraction = 2, message = "绩效评分格式不正确")
    private BigDecimal performanceScore;
    
    /**
     * 上次评估日期
     */
    private LocalDate lastEvaluationDate;
    
    /**
     * 下次评估日期
     */
    private LocalDate nextEvaluationDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 更新人ID
     */
    private Long updatedBy;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
    
    /**
     * 头像URL
     */
    @Size(max = 200, message = "头像URL长度不能超过200个字符")
    private String avatarUrl;
    
    /**
     * 工作状态统计（JSON格式存储）
     */
    private String workStatusStats;
    
    /**
     * 服务统计（JSON格式存储）
     */
    private String serviceStats;
    
    /**
     * 排班信息（JSON格式存储）
     */
    private String scheduleInfo;

    /**
     * 获取年龄
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * 获取工作年限
     */
    public Integer getWorkYears() {
        if (hireDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    /**
     * 是否需要体检
     */
    public boolean needsPhysicalExam() {
        if (nextPhysicalExamDate == null) {
            return true;
        }
        return LocalDate.now().isAfter(nextPhysicalExamDate);
    }

    /**
     * 是否需要培训
     */
    public boolean needsTraining() {
        if (nextTrainingDate == null) {
            return true;
        }
        return LocalDate.now().isAfter(nextTrainingDate);
    }

    /**
     * 是否即将过生日（30天内）
     */
    public boolean hasUpcomingBirthday() {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate thisYearBirthday = birthDate.withYear(today.getYear());
        if (thisYearBirthday.isBefore(today)) {
            thisYearBirthday = thisYearBirthday.plusYears(1);
        }
        return thisYearBirthday.isBefore(today.plusDays(30));
    }

    /**
     * 是否即将工作周年（30天内）
     */
    public boolean hasUpcomingWorkAnniversary() {
        if (hireDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate thisYearAnniversary = hireDate.withYear(today.getYear());
        if (thisYearAnniversary.isBefore(today)) {
            thisYearAnniversary = thisYearAnniversary.plusYears(1);
        }
        return thisYearAnniversary.isBefore(today.plusDays(30));
    }
    
    // 手动添加status的getter和setter方法
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // 添加更多需要的getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTrainingRecords(String trainingRecords) {
        this.trainingRecords = trainingRecords;
    }

    public String getTrainingRecords() {
        return trainingRecords;
    }

    public void setLastTrainingDate(LocalDate lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }

    public LocalDate getLastTrainingDate() {
        return lastTrainingDate;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setLastPhysicalExamDate(LocalDate lastPhysicalExamDate) {
        this.lastPhysicalExamDate = lastPhysicalExamDate;
    }

    public LocalDate getLastPhysicalExamDate() {
        return lastPhysicalExamDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyRelation() {
        return emergencyRelation;
    }

    public void setEmergencyRelation(String emergencyRelation) {
        this.emergencyRelation = emergencyRelation;
    }

    public LocalDate getLastEvaluationDate() {
        return lastEvaluationDate;
    }

    public void setLastEvaluationDate(LocalDate lastEvaluationDate) {
        this.lastEvaluationDate = lastEvaluationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}