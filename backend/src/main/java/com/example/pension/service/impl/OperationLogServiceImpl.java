package com.example.pension.service.impl;

import com.example.pension.dto.OperationLogDTO;
import com.example.pension.mapper.OperationLogDTOMapper;
import com.example.pension.mapper.OperationLogMapper;
import com.example.pension.model.OperationLog;
import com.example.pension.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    private final OperationLogDTOMapper operationLogDTOMapper;
    
    @Override
    public void createLog(String username, Long userId, String operationType, String operationDesc,
                         String module, String logLevel, String ipAddress, String userAgent,
                         String requestUrl, String requestMethod, String requestParams,
                         String responseData, Integer requestTime, String errorMessage, String errorStack) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUsername(username);
            operationLog.setUserId(userId);
            operationLog.setOperationType(operationType);
            operationLog.setOperationDesc(operationDesc);
            operationLog.setModule(module);
            operationLog.setLogLevel(logLevel != null ? logLevel : "INFO");
            operationLog.setIpAddress(ipAddress);
            operationLog.setUserAgent(userAgent);
            operationLog.setRequestUrl(requestUrl);
            operationLog.setRequestMethod(requestMethod);
            operationLog.setRequestParams(requestParams);
            operationLog.setResponseData(responseData);
            operationLog.setRequestTime(requestTime);
            operationLog.setErrorMessage(errorMessage);
            operationLog.setErrorStack(errorStack);
            operationLog.setCreateTime(LocalDateTime.now());
            
            operationLogMapper.insert(operationLog);
            log.debug("操作日志创建成功: {}", operationDesc);
        } catch (Exception e) {
            log.error("创建操作日志失败: {}", e.getMessage(), e);
            // 这里不抛出异常，避免影响主业务流程
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public OperationLogDTO getLogById(Long id) {
        OperationLog operationLog = operationLogMapper.findById(id);
        if (operationLog == null) {
            throw new RuntimeException("操作日志不存在，ID: " + id);
        }
        return operationLogDTOMapper.toDTO(operationLog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLogsByConditions(String username, String operationType, String logLevel,
                                                   String module, LocalDateTime startTime, LocalDateTime endTime,
                                                   int page, int size) {
        // 计算分页参数
        int offset = (page - 1) * size;
        
        // 查询列表数据
        List<OperationLog> logs = operationLogMapper.findByConditions(
            username, operationType, logLevel, module, startTime, endTime, offset, size);
        
        // 查询总数
        long total = operationLogMapper.countByConditions(
            username, operationType, logLevel, module, startTime, endTime);
        
        // 转换为DTO
        List<OperationLogDTO> logDTOs = operationLogDTOMapper.toDTOList(logs);
        
        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", logDTOs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        
        return result;
    }
    
    @Override
    public void deleteLog(Long id) {
        OperationLog operationLog = operationLogMapper.findById(id);
        if (operationLog == null) {
            throw new RuntimeException("操作日志不存在，ID: " + id);
        }
        
        int deleted = operationLogMapper.deleteById(id);
        if (deleted == 0) {
            throw new RuntimeException("删除操作日志失败，ID: " + id);
        }
        
        log.info("操作日志删除成功，ID: {}", id);
    }
    
    @Override
    public void deleteLogsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }
        
        int deleted = operationLogMapper.deleteByIds(ids);
        log.info("批量删除操作日志成功，删除数量: {}", deleted);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        return operationLogMapper.getStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogCountByDate() {
        return operationLogMapper.getLogCountByDate();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogCountByOperationType() {
        return operationLogMapper.getLogCountByOperationType();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogCountByModule() {
        return operationLogMapper.getLogCountByModule();
    }
} 