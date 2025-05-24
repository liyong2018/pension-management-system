package com.example.pension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRecordDTO {
    
    private Long id;
    
    @NotNull(message = "老人ID不能为空")
    private Long elderlyId;
    
    private String elderlyName;
    
    @NotBlank(message = "服务内容不能为空")
    private String serviceContent;
    
    @NotNull(message = "服务时间不能为空")
    private LocalDateTime serviceTime;
    
    private String serviceAddress;
    
    private String serviceProviderType;
    
    private Long serviceProviderId;
    
    private String serviceProviderName;
    
    private BigDecimal workOrderPrice;
    
    private String status;
    
    @Min(value = 1, message = "评价分数最低为1分")
    @Max(value = 5, message = "评价分数最高为5分")
    private Integer evaluationScore;
    
    private String evaluationComment;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 