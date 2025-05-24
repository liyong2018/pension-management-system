package com.example.pension.controller;

import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.service.ElderlyFamilyMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elderly-profiles/{elderlyId}/family-members")
@RequiredArgsConstructor
public class ElderlyFamilyMemberController {
    
    private final ElderlyFamilyMemberService familyMemberService;
    
    @GetMapping
    public ResponseEntity<List<ElderlyFamilyMemberDTO>> getAllByElderlyId(@PathVariable Long elderlyId) {
        return ResponseEntity.ok(familyMemberService.getAllByElderlyId(elderlyId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ElderlyFamilyMemberDTO> getById(@PathVariable Long elderlyId, @PathVariable Long id) {
        return ResponseEntity.ok(familyMemberService.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<ElderlyFamilyMemberDTO> create(
            @PathVariable Long elderlyId,
            @Valid @RequestBody ElderlyFamilyMemberDTO familyMemberDTO) {
        return new ResponseEntity<>(familyMemberService.create(elderlyId, familyMemberDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ElderlyFamilyMemberDTO> update(
            @PathVariable Long elderlyId,
            @PathVariable Long id,
            @Valid @RequestBody ElderlyFamilyMemberDTO familyMemberDTO) {
        return ResponseEntity.ok(familyMemberService.update(id, familyMemberDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long elderlyId, @PathVariable Long id) {
        familyMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllByElderlyId(@PathVariable Long elderlyId) {
        familyMemberService.deleteAllByElderlyId(elderlyId);
        return ResponseEntity.noContent().build();
    }
}