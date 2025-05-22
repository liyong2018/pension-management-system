package com.example.pension.service.impl;

import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.mapper.ElderlyFamilyMemberMapper;
import com.example.pension.model.ElderlyFamilyMember;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.repository.ElderlyFamilyMemberRepository;
import com.example.pension.repository.ElderlyProfileRepository;
import com.example.pension.service.ElderlyFamilyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElderlyFamilyMemberServiceImpl implements ElderlyFamilyMemberService {
    
    private final ElderlyFamilyMemberRepository familyMemberRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final ElderlyFamilyMemberMapper familyMemberMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ElderlyFamilyMemberDTO> getAllByElderlyId(Long elderlyId) {
        List<ElderlyFamilyMember> familyMembers = familyMemberRepository.findByElderlyProfileId(elderlyId);
        return familyMembers.stream()
                .map(familyMemberMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ElderlyFamilyMemberDTO getById(Long id) {
        ElderlyFamilyMember familyMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("家属信息不存在"));
        return familyMemberMapper.toDTO(familyMember);
    }
    
    @Override
    @Transactional
    public ElderlyFamilyMemberDTO create(Long elderlyId, ElderlyFamilyMemberDTO familyMemberDTO) {
        ElderlyProfile elderlyProfile = elderlyProfileRepository.findById(elderlyId)
                .orElseThrow(() -> new ResourceNotFoundException("老人档案不存在"));
        
        ElderlyFamilyMember familyMember = familyMemberMapper.toEntity(familyMemberDTO);
        familyMember.setElderlyProfile(elderlyProfile);
        
        ElderlyFamilyMember savedFamilyMember = familyMemberRepository.save(familyMember);
        return familyMemberMapper.toDTO(savedFamilyMember);
    }
    
    @Override
    @Transactional
    public ElderlyFamilyMemberDTO update(Long id, ElderlyFamilyMemberDTO familyMemberDTO) {
        ElderlyFamilyMember familyMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("家属信息不存在"));
        
        familyMemberMapper.updateEntityFromDTO(familyMemberDTO, familyMember);
        ElderlyFamilyMember updatedFamilyMember = familyMemberRepository.save(familyMember);
        
        return familyMemberMapper.toDTO(updatedFamilyMember);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (!familyMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("家属信息不存在");
        }
        familyMemberRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteAllByElderlyId(Long elderlyId) {
        familyMemberRepository.deleteByElderlyProfileId(elderlyId);
    }
} 