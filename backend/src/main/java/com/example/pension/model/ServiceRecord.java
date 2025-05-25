package com.example.pension.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRecord {
    
    private Long id;
    
    private Long elderlyId;
    
    private String elderlyName;
    
    private String serviceContent;
    
    private LocalDateTime serviceTime;
    
    private String serviceAddress;
    
    private String serviceType;
    
    private BigDecimal serviceDuration;
    
    private String serviceProviderType;
    
    private Long serviceProviderId;
    
    private String serviceProviderName;
    
    private BigDecimal workOrderPrice;
    
    private String status;
    
    private Integer evaluationScore;
    
    private String evaluationComment;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 关联对象
    private ElderlyProfile elderlyProfile;
} 