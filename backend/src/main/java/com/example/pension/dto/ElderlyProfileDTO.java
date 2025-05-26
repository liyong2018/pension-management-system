package com.example.pension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ElderlyProfileDTO {
    
    private Long id;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    private String gender;
    
    private LocalDate birthDate;
    
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    private String idCardNumber;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String photoUrl;
    
    private String addressDetail;
    
    private String community;
    
    private String pensionType;
    
    private String elderlyType;
    
    private String elderlyTypeLabel;
    
    private String medicalHistory;
    
    private String allergyHistory;
    
    private String physicalExamReport;
    
    private String currentHealthStatus;
    
    private String careLevel;
    
    private String abilityAssessment;
    
    private String livingHabits;
    
    private String hobbies;
    
    private String religiousBelief;
    
    private String remarks;
    
    private Long organizationId;
    
    private String organizationName;
    
    private List<ElderlyFamilyMemberDTO> familyMembers = new ArrayList<>();
} 