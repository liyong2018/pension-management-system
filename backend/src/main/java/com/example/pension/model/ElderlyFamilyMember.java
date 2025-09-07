package com.example.pension.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElderlyFamilyMember {
    
    private Long id;
    
    private ElderlyProfile elderlyProfile;
    
    private String name;
    
    private String relationship;
    
    private String phone;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;

    // Getter and Setter methods
    public ElderlyProfile getElderlyProfile() {
        return elderlyProfile;
    }

    public void setElderlyProfile(ElderlyProfile elderlyProfile) {
        this.elderlyProfile = elderlyProfile;
    }
}