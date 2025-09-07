package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 陌生人抓拍记录DTO
 * 用于数据传输
 */
@Data
public class FaceStrangerRecordDTO {
    
    private Long id;
    
    private Integer snapId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime snapTime;
    
    private String imagePath;
    
    private Double temperature;
    
    private String deviceId;
    
    private Long smartDeviceId;
    
    private String smartDeviceName; // 设备名称
    
    private Long organizationId;
    
    private String organizationName; // 机构名称
    
    private String processStatus;
    
    private String processStatusText; // 处理状态文本描述
    
    private String processResult;
    
    private String processResultText; // 处理结果文本描述
    
    private Long processedBy;
    
    private String processedByName; // 处理人姓名
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;
    
    private String remarks;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    public void setProcessStatusDesc(String processStatusDesc) {
        this.processStatusText = processStatusDesc;
    }
    
    public String getProcessStatusDesc() {
        return processStatusText;
    }
}