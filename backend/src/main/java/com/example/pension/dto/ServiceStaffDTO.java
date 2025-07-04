package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class ServiceStaffDTO {

    private Long id;

    // 基本信息
    private String employeeId; // 工号
    
    private String name; // 姓名
    
    private String gender; // 性别
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; // 出生日期
    
    private String phone; // 电话
    
    private String email; // 邮箱
    
    private String idCard; // 身份证号
    
    private String address; // 地址
    
    // 工作信息
    private String position; // 职位
    
    private String department; // 部门
    
    private String workType; // 工作类型（全职/兼职）
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate; // 入职日期
    
    private String status; // 状态（在职/离职/休假）
    
    private BigDecimal salary; // 薪资
    
    private String workShift; // 班次
    
    // 机构信息
    private Long organizationId;
    
    private String organizationName;
    
    // 专业信息
    private String education; // 学历
    
    private String major; // 专业
    
    private String certifications; // 资格证书
    
    private String skills; // 技能
    
    private Integer workExperience; // 工作经验（年）
    
    // 紧急联系人
    private String emergencyContact; // 紧急联系人姓名
    
    private String emergencyPhone; // 紧急联系人电话
    
    private String emergencyRelation; // 与紧急联系人关系
    
    // 健康信息
    private String healthStatus; // 健康状态
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastPhysicalExam; // 最后体检日期
    
    private String medicalHistory; // 病史
    
    // 培训信息
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastTrainingDate; // 最后培训日期
    
    private String trainingRecords; // 培训记录
    
    // 评价信息
    private BigDecimal performanceScore; // 绩效评分
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastEvaluationDate; // 最后评价日期
    
    private String evaluationComments; // 评价意见
    
    // 系统信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    private String createdBy; // 创建人
    
    private String updatedBy; // 更新人
    
    // 备注
    private String remarks; // 备注
    
    // 头像
    private String avatar; // 头像URL
    
    // 工作状态统计（用于前端展示）
    private Integer totalWorkDays; // 总工作天数
    
    private Integer attendanceDays; // 出勤天数
    
    private Integer leaveDays; // 请假天数
    
    private BigDecimal attendanceRate; // 出勤率
    
    // 服务统计（用于前端展示）
    private Integer totalServiceHours; // 总服务时长
    
    private Integer serviceCount; // 服务次数
    
    private BigDecimal customerSatisfaction; // 客户满意度
    
    // 排班信息（用于前端展示）
    private String currentShift; // 当前班次
    
    private String nextShift; // 下一班次
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextShiftTime; // 下一班次时间
    
    // 年龄（计算字段）
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    // 工作年限（计算字段）
    public Integer getWorkYears() {
        if (hireDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - hireDate.getYear();
    }
}