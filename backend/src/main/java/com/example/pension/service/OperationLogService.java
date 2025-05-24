package com.example.pension.service;

import com.example.pension.dto.OperationLogDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OperationLogService {
    
    /**
     * 创建操作日志
     */
    void createLog(String username, Long userId, String operationType, String operationDesc, 
                   String module, String logLevel, String ipAddress, String userAgent,
                   String requestUrl, String requestMethod, String requestParams, 
                   String responseData, Integer requestTime, String errorMessage, String errorStack);
    
    /**
     * 根据ID获取操作日志
     */
    OperationLogDTO getLogById(Long id);
    
    /**
     * 分页查询操作日志
     */
    Map<String, Object> getLogsByConditions(String username, String operationType, String logLevel,
                                           String module, LocalDateTime startTime, LocalDateTime endTime,
                                           int page, int size);
    
    /**
     * 删除操作日志
     */
    void deleteLog(Long id);
    
    /**
     * 批量删除操作日志
     */
    void deleteLogsBatch(List<Long> ids);
    
    /**
     * 获取统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 按日期统计日志数量
     */
    List<Map<String, Object>> getLogCountByDate();
    
    /**
     * 按操作类型统计
     */
    List<Map<String, Object>> getLogCountByOperationType();
    
    /**
     * 按模块统计
     */
    List<Map<String, Object>> getLogCountByModule();
} 