package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SmartDeviceDTO {

    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private String deviceBrand;

    private String deviceModel;

    private String deviceStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private String installationLocation;

    private Long elderlyId;

    private String elderlyName; // 绑定老人姓名

    private Long organizationId;

    private String organizationName; // 机构名称

    // 配置信息
    private String ipAddress;

    private String macAddress;

    private String wifiConfig;

    private String communicationProtocol;

    private Integer dataCollectionFrequency;

    private String alarmThreshold;

    private Integer batteryThreshold;

    // 监控信息
    private String realTimeData;

    private Integer batteryLevel;

    private Integer signalStrength;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCommunicationTime;

    private String dataUploadStatus;

    // 维护信息
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyExpiryDate;

    private Integer maintenanceCycle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextMaintenanceDate;

    private Integer failureCount;

    // 系统字段
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createdBy;

    private String updatedBy;
} 