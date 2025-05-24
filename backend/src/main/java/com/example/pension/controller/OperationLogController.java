package com.example.pension.controller;

import com.example.pension.dto.OperationLogDTO;
import com.example.pension.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Map<String, Object> result = operationLogService.getLogsByConditions(
                username, operationType, logLevel, module, startTime, endTime, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取操作日志详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperationLogDTO> getOperationLogById(@PathVariable Long id) {
        try {
            OperationLogDTO operationLog = operationLogService.getLogById(id);
            return ResponseEntity.ok(operationLog);
        } catch (Exception e) {
            log.error("获取操作日志详情失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOperationLog(@PathVariable Long id) {
        try {
            operationLogService.deleteLog(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
        } catch (Exception e) {
            log.error("删除操作日志失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteOperationLogsBatch(@RequestBody List<Long> ids) {
        try {
            operationLogService.deleteLogsBatch(ids);
            return ResponseEntity.ok(Map.of("success", true, "message", "批量删除成功"));
        } catch (Exception e) {
            log.error("批量删除操作日志失败，IDs: {}", ids, e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = operationLogService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("获取操作日志统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 按日期统计日志数量
     */
    @GetMapping("/statistics/date")
    public ResponseEntity<List<Map<String, Object>>> getLogCountByDate() {
        try {
            List<Map<String, Object>> result = operationLogService.getLogCountByDate();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("按日期统计日志数量失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 按操作类型统计
     */
    @GetMapping("/statistics/operation-type")
    public ResponseEntity<List<Map<String, Object>>> getLogCountByOperationType() {
        try {
            List<Map<String, Object>> result = operationLogService.getLogCountByOperationType();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("按操作类型统计失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 按模块统计
     */
    @GetMapping("/statistics/module")
    public ResponseEntity<List<Map<String, Object>>> getLogCountByModule() {
        try {
            List<Map<String, Object>> result = operationLogService.getLogCountByModule();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("按模块统计失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 