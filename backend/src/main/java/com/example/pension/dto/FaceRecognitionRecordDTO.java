package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人脸识别记录DTO
 * 用于数据传输
 */
@Data
public class FaceRecognitionRecordDTO {
    
    private Long id;
    
    private String customId;
    
    private Integer recordId;
    
    private Integer verifyStatus;
    
    private String verifyStatusText; // 认证结果文本描述
    
    private Double similarity;
    
    private String imagePath;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recognitionTime;
    
    private Double temperature;
    
    private String deviceId;
    
    private Long smartDeviceId;
    
    private String smartDeviceName; // 设备名称
    
    private Long elderlyId;
    
    private String elderlyName; // 老人姓名
    
    private Long organizationId;
    
    private String organizationName; // 机构名称
    
    private String processStatus;
    
    private String processStatusText; // 处理状态文本描述
    
    private String remarks;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // Additional setter method for compatibility
    public void setVerifyStatusDesc(String verifyStatusDesc) {
        this.verifyStatusText = verifyStatusDesc;
    }

    public String getVerifyStatusDesc() {
        return this.verifyStatusText;
    }
}