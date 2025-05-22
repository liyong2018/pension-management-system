package com.example.pension.service;

import com.example.pension.dto.ElderlyFamilyMemberDTO;

import java.util.List;

public interface ElderlyFamilyMemberService {
    
    List<ElderlyFamilyMemberDTO> getAllByElderlyId(Long elderlyId);
    
    ElderlyFamilyMemberDTO getById(Long id);
    
    ElderlyFamilyMemberDTO create(Long elderlyId, ElderlyFamilyMemberDTO familyMemberDTO);
    
    ElderlyFamilyMemberDTO update(Long id, ElderlyFamilyMemberDTO familyMemberDTO);
    
    void delete(Long id);
    
    void deleteAllByElderlyId(Long elderlyId);
} 