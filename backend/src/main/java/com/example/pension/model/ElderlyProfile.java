package com.example.pension.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "elderly_profile")
public class ElderlyProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(length = 10)
    private String gender;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "id_card_number", unique = true, length = 18)
    private String idCardNumber;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "address_detail")
    private String addressDetail;
    
    @Column(length = 100)
    private String community;
    
    @Column(name = "pension_type", length = 50)
    private String pensionType;
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;
    
    @Column(name = "physical_exam_report", columnDefinition = "TEXT")
    private String physicalExamReport;
    
    @Column(name = "current_health_status", columnDefinition = "TEXT")
    private String currentHealthStatus;
    
    @Column(name = "care_level", length = 50)
    private String careLevel;
    
    @Column(name = "ability_assessment", length = 50)
    private String abilityAssessment;
    
    @Column(name = "living_habits", columnDefinition = "TEXT")
    private String livingHabits;
    
    @Column(columnDefinition = "TEXT")
    private String hobbies;
    
    @Column(name = "religious_belief", length = 100)
    private String religiousBelief;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;
    
    @OneToMany(mappedBy = "elderlyProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElderlyFamilyMember> familyMembers = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 添加/移除家属的便捷方法
    public void addFamilyMember(ElderlyFamilyMember familyMember) {
        familyMembers.add(familyMember);
        familyMember.setElderlyProfile(this);
    }
    
    public void removeFamilyMember(ElderlyFamilyMember familyMember) {
        familyMembers.remove(familyMember);
        familyMember.setElderlyProfile(null);
    }
} 