package com.example.pension.controller;

import com.example.pension.dto.ServiceRecordDTO;
import com.example.pension.service.ServiceRecordService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-records")
@RequiredArgsConstructor
public class ServiceRecordController {
    
    private final ServiceRecordService serviceRecordService;
    
    @GetMapping
    public ResponseEntity<PageInfo<ServiceRecordDTO>> getAllOrSearch(
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String serviceContent,
            @RequestParam(required = false) String serviceProviderName,
            @RequestParam(required = false) String serviceProviderType,
            @RequestParam(required = false) Long serviceProviderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        PageInfo<ServiceRecordDTO> page;
        
        // Check if any search criteria is provided
        boolean hasSearchCriteria = StringUtils.hasText(elderlyName) || 
                                   StringUtils.hasText(serviceContent) || 
                                   StringUtils.hasText(serviceProviderName) || 
                                   StringUtils.hasText(serviceProviderType) ||
                                   serviceProviderId != null ||
                                   StringUtils.hasText(status) ||
                                   startTime != null ||
                                   endTime != null;

        if (hasSearchCriteria) {
            page = serviceRecordService.searchByMultipleConditions(
                    elderlyName, serviceContent, serviceProviderName, serviceProviderType,
                    serviceProviderId, status, startTime, endTime, pageNum, pageSize);
        } else {
            page = serviceRecordService.getAll(pageNum, pageSize);
        }
        
        return ResponseEntity.ok(page);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRecordService.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<ServiceRecordDTO> create(@Valid @RequestBody ServiceRecordDTO serviceRecordDTO) {
        return new ResponseEntity<>(serviceRecordService.create(serviceRecordDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceRecordDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRecordDTO serviceRecordDTO) {
        return ResponseEntity.ok(serviceRecordService.update(id, serviceRecordDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        serviceRecordService.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/elderly/{elderlyId}")
    public ResponseEntity<List<ServiceRecordDTO>> getByElderlyId(@PathVariable Long elderlyId) {
        return ResponseEntity.ok(serviceRecordService.getByElderlyId(elderlyId));
    }
    
    @GetMapping("/status-statistics")
    public ResponseEntity<Map<String, Long>> getStatusStatistics() {
        return ResponseEntity.ok(serviceRecordService.getStatusStatistics());
    }
    
    @GetMapping("/provider-type-statistics")
    public ResponseEntity<Map<String, Long>> getServiceProviderTypeStatistics() {
        return ResponseEntity.ok(serviceRecordService.getServiceProviderTypeStatistics());
    }
    
    @GetMapping("/for-evaluation")
    public ResponseEntity<List<ServiceRecordDTO>> getRecordsForEvaluation() {
        return ResponseEntity.ok(serviceRecordService.getRecordsForEvaluation());
    }
    
    @PutMapping("/{id}/evaluate")
    public ResponseEntity<ServiceRecordDTO> evaluate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> evaluationData) {
        Integer score = (Integer) evaluationData.get("score");
        String comment = (String) evaluationData.get("comment");
        return ResponseEntity.ok(serviceRecordService.evaluate(id, score, comment));
    }
} 