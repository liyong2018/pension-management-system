package com.example.pension.controller;

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
    public ResponseEntity<PageInfo<ElderlyProfileDTO>> getAllOrSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String idCardNumber,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long organizationId, // Matched service layer
            @RequestParam(defaultValue = "1") int pageNum,      // PageHelper is 1-based
            @RequestParam(defaultValue = "10") int pageSize) {
        
        PageInfo<ElderlyProfileDTO> page;
        
        // Check if any search criteria is provided
        boolean hasSearchCriteria = StringUtils.hasText(name) || 
                                   StringUtils.hasText(idCardNumber) || 
                                   StringUtils.hasText(phone) || 
                                   organizationId != null;

        if (hasSearchCriteria) {
            page = elderlyProfileService.searchByMultipleConditions(name, idCardNumber, phone, organizationId, pageNum, pageSize);
        } else {
            page = elderlyProfileService.getAll(pageNum, pageSize);
        }
        
        return ResponseEntity.ok(page);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ElderlyProfileDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(elderlyProfileService.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<ElderlyProfileDTO> create(@Valid @RequestBody ElderlyProfileDTO elderlyProfileDTO) {
        return new ResponseEntity<>(elderlyProfileService.create(elderlyProfileDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ElderlyProfileDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ElderlyProfileDTO elderlyProfileDTO) {
        return ResponseEntity.ok(elderlyProfileService.update(id, elderlyProfileDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        elderlyProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        elderlyProfileService.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pension-type-statistics")
    public ResponseEntity<Map<String, Long>> getPensionTypeStatistics() {
        return ResponseEntity.ok(elderlyProfileService.getPensionTypeStatistics());
    }
    
    @GetMapping("/ability-assessment-statistics")
    public ResponseEntity<Map<String, Long>> getAbilityAssessmentStatistics() {
        return ResponseEntity.ok(elderlyProfileService.getAbilityAssessmentStatistics());
    }
    
    @GetMapping("/by-organization/{organizationId}")
    public ResponseEntity<List<ElderlyProfileDTO>> getByOrganizationId(@PathVariable Long organizationId) {
        return ResponseEntity.ok(elderlyProfileService.getByOrganizationId(organizationId));
    }
} 