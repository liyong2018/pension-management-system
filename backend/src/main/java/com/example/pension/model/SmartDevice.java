package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SmartDevice {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private String deviceBrand;

    private String deviceModel;

    private String deviceStatus = "在线";

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private String installationLocation;

    private ElderlyProfile elderlyProfile;

    private Organization organization;

    // 配置信息
    private String ipAddress;

    private String macAddress;

    private String wifiConfig;

    private String communicationProtocol;

    private Integer dataCollectionFrequency = 60;

    private String alarmThreshold;

    private Integer batteryThreshold = 20;

    // 监控信息
    private String realTimeData;

    private Integer batteryLevel;

    private Integer signalStrength;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCommunicationTime;

    private String dataUploadStatus = "正常";

    // 维护信息
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyExpiryDate;

    private Integer maintenanceCycle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextMaintenanceDate;

    private Integer failureCount = 0;

    // 系统字段
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createdBy;

    private String updatedBy;

    private Boolean isDeleted = false;
    
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    
    public String getDeviceCode() {
        return deviceCode;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
    
    public String getDeviceStatus() {
        return deviceStatus;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceBrand() {
        return deviceBrand;
    }
    
    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getInstallationLocation() {
        return installationLocation;
    }
    
    public void setInstallationLocation(String installationLocation) {
        this.installationLocation = installationLocation;
    }
    
    public ElderlyProfile getElderlyProfile() {
        return elderlyProfile;
    }
    
    public void setElderlyProfile(ElderlyProfile elderlyProfile) {
        this.elderlyProfile = elderlyProfile;
    }
    
    public Organization getOrganization() {
        return organization;
    }
    
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}