package com.example.pension.service.impl;

import com.example.pension.dao.ElderlyFamilyMemberDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.mapper.ElderlyFamilyMemberMapper;
import com.example.pension.model.ElderlyFamilyMember;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.service.ElderlyFamilyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElderlyFamilyMemberServiceImpl implements ElderlyFamilyMemberService {
    
    private final ElderlyFamilyMemberDao familyMemberDao;
    private final ElderlyProfileDao elderlyProfileDao;
    private final ElderlyFamilyMemberMapper familyMemberMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ElderlyFamilyMemberDTO> getAllByElderlyId(Long elderlyId) {
        List<ElderlyFamilyMember> familyMembers = familyMemberDao.findAllByElderlyId(elderlyId);
        return familyMembers.stream()
                .map(familyMemberMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ElderlyFamilyMemberDTO getById(Long id) {
        ElderlyFamilyMember familyMember = familyMemberDao.findById(id);
        if (familyMember == null) {
            throw new ResourceNotFoundException("家属信息不存在: " + id);
        }
        return familyMemberMapper.toDTO(familyMember);
    }
    
    @Override
    @Transactional
    public ElderlyFamilyMemberDTO create(Long elderlyId, ElderlyFamilyMemberDTO familyMemberDTO) {
        ElderlyProfile elderlyProfile = elderlyProfileDao.findById(elderlyId);
        if (elderlyProfile == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + elderlyId);
        }
        
        ElderlyFamilyMember familyMember = familyMemberMapper.toEntity(familyMemberDTO);
        familyMember.setElderlyProfile(elderlyProfile);
        
        familyMemberDao.insert(familyMember);
        return familyMemberMapper.toDTO(familyMember);
    }
    
    @Override
    @Transactional
    public ElderlyFamilyMemberDTO update(Long id, ElderlyFamilyMemberDTO familyMemberDTO) {
        ElderlyFamilyMember familyMember = familyMemberDao.findById(id);
        if (familyMember == null) {
            throw new ResourceNotFoundException("家属信息不存在: " + id);
        }
        
        familyMemberMapper.updateEntityFromDTO(familyMemberDTO, familyMember);
        familyMemberDao.update(familyMember);
        
        return familyMemberMapper.toDTO(familyMember);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (familyMemberDao.findById(id) == null) {
            throw new ResourceNotFoundException("家属信息不存在，无法删除: " + id);
        }
        familyMemberDao.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteAllByElderlyId(Long elderlyId) {
        familyMemberDao.deleteByElderlyId(elderlyId);
    }
} 