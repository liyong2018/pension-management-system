package com.example.pension.controller;

import com.example.pension.annotation.Loggable;
import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.service.OrganizationService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    @Loggable(module = "组织机构", operationType = "查询列表", description = "查询组织机构列表")
    public ResponseEntity<PageInfo<OrganizationDTO>> getAllOrganizations(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {
        PageInfo<OrganizationDTO> organizations = organizationService.getAllOrganizations(pageNum, pageSize, name);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    @Loggable(module = "组织机构", operationType = "查询详情", description = "查询组织机构详情")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Loggable(module = "组织机构", operationType = "新增", description = "新增组织机构")
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody CreateOrganizationDTO createOrganizationDTO) {
        OrganizationDTO createdOrganization = organizationService.createOrganization(createOrganizationDTO);
        return new ResponseEntity<>(createdOrganization, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Loggable(module = "组织机构", operationType = "修改", description = "修改组织机构信息")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long id, @Valid @RequestBody UpdateOrganizationDTO updateOrganizationDTO) {
        OrganizationDTO updatedOrganization = organizationService.updateOrganization(id, updateOrganizationDTO);
        return ResponseEntity.ok(updatedOrganization);
    }

    @DeleteMapping("/{id}")
    @Loggable(module = "组织机构", operationType = "删除", description = "删除组织机构")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/batch")
    @Loggable(module = "组织机构", operationType = "批量删除", description = "批量删除组织机构")
    public ResponseEntity<Void> deleteOrganizations(@RequestBody List<Long> ids) {
        organizationService.deleteOrganizations(ids);
        return ResponseEntity.noContent().build();
    }
} 