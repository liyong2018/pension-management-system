package com.example.pension.controller;

import com.example.pension.dto.CreateOrganizationDTO;
import com.example.pension.dto.OrganizationDTO;
import com.example.pension.dto.UpdateOrganizationDTO;
import com.example.pension.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationDTO>> getAllOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id,asc") String sort) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<OrganizationDTO> organizations = organizationService.getAllOrganizations(pageable, name);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody CreateOrganizationDTO createOrganizationDTO) {
        OrganizationDTO createdOrganization = organizationService.createOrganization(createOrganizationDTO);
        return new ResponseEntity<>(createdOrganization, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long id, @Valid @RequestBody UpdateOrganizationDTO updateOrganizationDTO) {
        OrganizationDTO updatedOrganization = organizationService.updateOrganization(id, updateOrganizationDTO);
        return ResponseEntity.ok(updatedOrganization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
} 