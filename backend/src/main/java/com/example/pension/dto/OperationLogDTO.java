package com.example.pension.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogDTO {
    
    private Long id;
    private String username;
    private Long userId;
    private String operationType;
    private String operationDesc;
    private String module;
    private String logLevel;
    private String ipAddress;
    private String userAgent;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String responseData;
    private Integer requestTime;
    private String errorMessage;
    private String errorStack;
    private LocalDateTime createTime;
    
    // 格式化的创建时间
    public String getFormattedCreateTime() {
        if (createTime == null) {
            return null;
        }
        return createTime.toString().replace("T", " ");
    }
} 