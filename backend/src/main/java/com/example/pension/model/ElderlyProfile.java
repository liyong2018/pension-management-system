package com.example.pension.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ElderlyProfile {
    
    private Long id;
    
    private String name;
    
    private String gender;
    
    private LocalDate birthDate;
    
    private String idCardNumber;
    
    private String customId;
    
    private String phone;
    
    private String photoUrl;
    
    private String addressDetail;
    
    private String community;
    
    private String pensionType;
    
    private String elderlyType;
    
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
    
    private Organization organization;
    
    private List<ElderlyFamilyMember> familyMembers = new ArrayList<>();
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 添加/移除家属的便捷方法
    public void addFamilyMember(ElderlyFamilyMember familyMember) {
        familyMembers.add(familyMember);
        familyMember.setElderlyProfile(this);
    }
    
    public void removeFamilyMember(ElderlyFamilyMember familyMember) {
        this.familyMembers.remove(familyMember);
        familyMember.setElderlyProfile(null);
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrganizationId() {
        return organization != null ? organization.getId() : null;
    }
}