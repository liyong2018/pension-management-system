package com.example.pension.controller;

import com.example.pension.annotation.Loggable;
import com.example.pension.dto.ElderlyProfileDTO;
import com.example.pension.service.ElderlyProfileService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils; // For checking empty strings

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elderly-profiles")
@RequiredArgsConstructor
public class ElderlyProfileController {
    
    private final ElderlyProfileService elderlyProfileService;
    
    @GetMapping
    @Loggable(module = "人员档案", operationType = "查询列表", description = "查询人员档案列表")
    public ResponseEntity<PageInfo<ElderlyProfileDTO>> getAllOrSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String idCardNumber,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String elderlyType,
            @RequestParam(required = false) Long organizationId, // Matched service layer
            @RequestParam(defaultValue = "1") int pageNum,      // PageHelper is 1-based
            @RequestParam(defaultValue = "10") int pageSize) {
        
        PageInfo<ElderlyProfileDTO> page;
        
        // Check if any search criteria is provided
        boolean hasSearchCriteria = StringUtils.hasText(name) || 
                                   StringUtils.hasText(idCardNumber) || 
                                   StringUtils.hasText(phone) || 
                                   StringUtils.hasText(elderlyType) ||
                                   organizationId != null;

        if (hasSearchCriteria) {
            page = elderlyProfileService.searchByMultipleConditions(name, idCardNumber, phone, elderlyType, organizationId, pageNum, pageSize);
        } else {
            page = elderlyProfileService.getAll(pageNum, pageSize);
        }
        
        return ResponseEntity.ok(page);
    }
    
    @GetMapping("/{id}")
    @Loggable(module = "人员档案", operationType = "查询详情", description = "查询人员档案详情")
    public ResponseEntity<ElderlyProfileDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(elderlyProfileService.getById(id));
    }
    
    @PostMapping
    @Loggable(module = "人员档案", operationType = "新增", description = "新增人员档案")
    public ResponseEntity<ElderlyProfileDTO> create(@Valid @RequestBody ElderlyProfileDTO elderlyProfileDTO) {
        return new ResponseEntity<>(elderlyProfileService.create(elderlyProfileDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Loggable(module = "人员档案", operationType = "修改", description = "修改人员档案")
    public ResponseEntity<ElderlyProfileDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ElderlyProfileDTO elderlyProfileDTO) {
        return ResponseEntity.ok(elderlyProfileService.update(id, elderlyProfileDTO));
    }
    
    @DeleteMapping("/{id}")
    @Loggable(module = "人员档案", operationType = "删除", description = "删除人员档案")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        elderlyProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/batch")
    @Loggable(module = "人员档案", operationType = "批量删除", description = "批量删除人员档案")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        elderlyProfileService.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pension-type-statistics")
    @Loggable(module = "人员档案", operationType = "统计查询", description = "查询养老类型统计")
    public ResponseEntity<Map<String, Long>> getPensionTypeStatistics() {
        return ResponseEntity.ok(elderlyProfileService.getPensionTypeStatistics());
    }
    
    @GetMapping("/ability-assessment-statistics")
    @Loggable(module = "人员档案", operationType = "统计查询", description = "查询能力评估统计")
    public ResponseEntity<Map<String, Long>> getAbilityAssessmentStatistics() {
        return ResponseEntity.ok(elderlyProfileService.getAbilityAssessmentStatistics());
    }
    
    @GetMapping("/by-organization/{organizationId}")
    @Loggable(module = "人员档案", operationType = "查询列表", description = "根据机构查询人员档案")
    public ResponseEntity<List<ElderlyProfileDTO>> getByOrganizationId(@PathVariable Long organizationId) {
        return ResponseEntity.ok(elderlyProfileService.getByOrganizationId(organizationId));
    }
} 