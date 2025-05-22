package com.example.pension.repository;

import com.example.pension.model.ElderlyFamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElderlyFamilyMemberRepository extends JpaRepository<ElderlyFamilyMember, Long> {
    
    List<ElderlyFamilyMember> findByElderlyProfileId(Long elderlyId);
    
    void deleteByElderlyProfileId(Long elderlyId);
} 