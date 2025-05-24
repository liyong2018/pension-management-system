package com.example.pension.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Volunteer {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String idCardNumber;
    private String phone;
    private String email;
    private String addressDetail;
    private String householdAddress;
    private String education;
    private String graduationSchool;
    private String occupation;
    private String workUnit;
    private String professionalSkills;
    private String hobbies;
    private String languageAbility;
    private String serviceIntention;
    private String availableTime;
    private String serviceExperience;
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    private Date registrationDate;
    private String status;
    private BigDecimal totalServiceHours;
    private Integer points;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Volunteer() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getIdCardNumber() { return idCardNumber; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }

    public String getHouseholdAddress() { return householdAddress; }
    public void setHouseholdAddress(String householdAddress) { this.householdAddress = householdAddress; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getGraduationSchool() { return graduationSchool; }
    public void setGraduationSchool(String graduationSchool) { this.graduationSchool = graduationSchool; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getWorkUnit() { return workUnit; }
    public void setWorkUnit(String workUnit) { this.workUnit = workUnit; }

    public String getProfessionalSkills() { return professionalSkills; }
    public void setProfessionalSkills(String professionalSkills) { this.professionalSkills = professionalSkills; }

    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }

    public String getLanguageAbility() { return languageAbility; }
    public void setLanguageAbility(String languageAbility) { this.languageAbility = languageAbility; }

    public String getServiceIntention() { return serviceIntention; }
    public void setServiceIntention(String serviceIntention) { this.serviceIntention = serviceIntention; }

    public String getAvailableTime() { return availableTime; }
    public void setAvailableTime(String availableTime) { this.availableTime = availableTime; }

    public String getServiceExperience() { return serviceExperience; }
    public void setServiceExperience(String serviceExperience) { this.serviceExperience = serviceExperience; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalServiceHours() { return totalServiceHours; }
    public void setTotalServiceHours(BigDecimal totalServiceHours) { this.totalServiceHours = totalServiceHours; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
} 