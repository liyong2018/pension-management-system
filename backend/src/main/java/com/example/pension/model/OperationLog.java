package com.example.pension.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    
    private Long id;
    private String username;
    private Long userId;
    private String operationType;
    private String operationDesc;
    private String module;
    private String logLevel = "INFO";
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
    
    public void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (logLevel == null) {
            logLevel = "INFO";
        }
    }
} 