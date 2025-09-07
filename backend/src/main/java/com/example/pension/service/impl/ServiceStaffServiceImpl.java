package com.example.pension.service.impl;

import com.example.pension.dao.ServiceStaffDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.dto.ServiceStaffDTO;
import com.example.pension.mapper.ServiceStaffDTOMapper;
import com.example.pension.model.ServiceStaff;
import com.example.pension.model.Organization;
import com.example.pension.service.ServiceStaffService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ServiceStaffServiceImpl implements ServiceStaffService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ServiceStaffServiceImpl.class);

    @Autowired
    private ServiceStaffDao serviceStaffDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public ServiceStaffDTO create(ServiceStaffDTO serviceStaffDTO) {
        // 验证工号是否重复
        ServiceStaff existingStaff = serviceStaffDao.findByEmployeeId(serviceStaffDTO.getEmployeeId());
        if (existingStaff != null) {
            throw new RuntimeException("工号已存在：" + serviceStaffDTO.getEmployeeId());
        }

        ServiceStaff serviceStaff = ServiceStaffDTOMapper.INSTANCE.toEntity(serviceStaffDTO);
        
        // 验证机构是否存在
        if (serviceStaffDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(serviceStaffDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + serviceStaffDTO.getOrganizationId());
            }
        }

        // 设置默认值
        if (serviceStaff.getStatus() == null) {
            serviceStaff.setStatus("ACTIVE");
        }
        if (serviceStaff.getCreatedAt() == null) {
            serviceStaff.setCreatedAt(LocalDateTime.now());
        }
        serviceStaff.setUpdatedAt(LocalDateTime.now());

        serviceStaffDao.insert(serviceStaff);
        return getById(serviceStaff.getId());
    }

    @Override
    public ServiceStaffDTO getById(Long id) {
        ServiceStaff serviceStaff = serviceStaffDao.findById(id);
        if (serviceStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        
        ServiceStaffDTO dto = ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaff);
        
        // 设置机构名称
        if (serviceStaff.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(serviceStaff.getOrganizationId());
            if (organization != null) {
                dto.setOrganizationName(organization.getName());
            }
        }
        
        return dto;
    }

    @Override
    public ServiceStaffDTO getByEmployeeId(String employeeId) {
        ServiceStaff serviceStaff = serviceStaffDao.findByEmployeeId(employeeId);
        if (serviceStaff == null) {
            return null;
        }
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaff);
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        return serviceStaffDao.findByEmployeeId(employeeId) != null;
    }

    @Override
    public PageInfo<ServiceStaffDTO> searchByConditions(
            String name, String employeeId, String position, String status, 
            Long organizationId, int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findWithConditions(
                name, employeeId, position, status, organizationId);
        PageInfo<ServiceStaff> pageInfo = new PageInfo<>(serviceStaffs);
        
        List<ServiceStaffDTO> serviceStaffDTOs = ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
        
        // 为每个服务人员设置机构名称
        for (ServiceStaffDTO dto : serviceStaffDTOs) {
            if (dto.getOrganizationId() != null) {
                Organization organization = organizationDao.findById(dto.getOrganizationId());
                if (organization != null) {
                    dto.setOrganizationName(organization.getName());
                }
            }
        }
        
        PageInfo<ServiceStaffDTO> result = new PageInfo<>();
        result.setList(serviceStaffDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public PageInfo<ServiceStaffDTO> searchByConditionsExtended(
            String name, String employeeId, String position, String department,
            String workType, String workShift, String status, 
            Long organizationId, int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize, "id desc");
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findWithConditionsExtended(
                name, employeeId, position, department, workType, workShift, status, organizationId);
        PageInfo<ServiceStaff> pageInfo = new PageInfo<>(serviceStaffs);
        
        List<ServiceStaffDTO> serviceStaffDTOs = ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
        
        // 为每个服务人员设置机构名称
        for (ServiceStaffDTO dto : serviceStaffDTOs) {
            if (dto.getOrganizationId() != null) {
                Organization organization = organizationDao.findById(dto.getOrganizationId());
                if (organization != null) {
                    dto.setOrganizationName(organization.getName());
                }
            }
        }
        
        PageInfo<ServiceStaffDTO> result = new PageInfo<>();
        result.setList(serviceStaffDTOs);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        
        return result;
    }

    @Override
    public ServiceStaffDTO update(Long id, ServiceStaffDTO serviceStaffDTO) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }

        // 验证工号是否重复（排除当前人员）
        if (serviceStaffDTO.getEmployeeId() != null && 
            !serviceStaffDTO.getEmployeeId().equals(existingStaff.getEmployeeId())) {
            ServiceStaff duplicateStaff = serviceStaffDao.findByEmployeeId(serviceStaffDTO.getEmployeeId());
            if (duplicateStaff != null) {
                throw new RuntimeException("工号已存在：" + serviceStaffDTO.getEmployeeId());
            }
        }

        // 更新字段
        ServiceStaffDTOMapper.INSTANCE.updateEntityFromDTO(serviceStaffDTO, existingStaff);
        
        // 验证机构是否存在
        if (serviceStaffDTO.getOrganizationId() != null) {
            Organization organization = organizationDao.findById(serviceStaffDTO.getOrganizationId());
            if (organization == null) {
                throw new RuntimeException("关联的机构不存在，ID：" + serviceStaffDTO.getOrganizationId());
            }
        }

        existingStaff.setUpdatedAt(LocalDateTime.now());
        serviceStaffDao.update(existingStaff);
        
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        serviceStaffDao.delete(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        serviceStaffDao.batchDelete(ids);
    }

    @Override
    public void changeStatus(Long id, String status) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        existingStaff.setStatus(status);
        existingStaff.setUpdatedAt(LocalDateTime.now());
        serviceStaffDao.update(existingStaff);
    }

    @Override
    public List<ServiceStaffDTO> getByOrganizationId(Long organizationId) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByOrganizationId(organizationId);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getByPosition(String position) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByPosition(position);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public Long countByOrganizationId(Long organizationId) {
        return serviceStaffDao.countByOrganizationId(organizationId);
    }

    @Override
    public Long countByPosition(String position) {
        return serviceStaffDao.countByPosition(position);
    }

    @Override
    public Map<String, Long> getStaffStatistics(Long organizationId) {
        Map<String, Long> stats = new HashMap<>();
        
        // 总人数
        Long totalCount = organizationId != null ? 
            serviceStaffDao.countByOrganizationId(organizationId) : 
            serviceStaffDao.countAll();
        stats.put("total", totalCount);
        
        // 在职人数
        Long activeCount = organizationId != null ? 
            serviceStaffDao.countByOrganizationIdAndStatus(organizationId, "ACTIVE") : 
            serviceStaffDao.countByStatus("ACTIVE");
        stats.put("active", activeCount);
        
        // 离职人数
        Long inactiveCount = organizationId != null ? 
            serviceStaffDao.countByOrganizationIdAndStatus(organizationId, "INACTIVE") : 
            serviceStaffDao.countByStatus("INACTIVE");
        stats.put("inactive", inactiveCount);
        
        // 停职人数
        Long suspendedCount = organizationId != null ? 
            serviceStaffDao.countByOrganizationIdAndStatus(organizationId, "SUSPENDED") : 
            serviceStaffDao.countByStatus("SUSPENDED");
        stats.put("suspended", suspendedCount);
        
        return stats;
    }

    @Override
    public Map<String, Object> getStaffSchedule(Long id, String startDate, String endDate) {
        // 这里应该实现排班查询逻辑
        // 暂时返回空的排班信息
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("staffId", id);
        schedule.put("startDate", startDate);
        schedule.put("endDate", endDate);
        schedule.put("schedules", List.of());
        return schedule;
    }

    @Override
    public byte[] exportStaffList(
            String name, String employeeId, String position, String status, Long organizationId) {
        // 这里应该实现Excel导出逻辑
        // 暂时返回空字节数组
        log.warn("导出功能尚未实现");
        return new byte[0];
    }

    @Override
    public Map<String, Object> importStaffData(MultipartFile file) throws Exception {
        // 这里应该实现Excel导入逻辑
        // 暂时返回成功结果
        log.warn("导入功能尚未实现");
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "导入功能尚未实现");
        result.put("successCount", 0);
        result.put("failCount", 0);
        return result;
    }

    @Override
    public List<ServiceStaffDTO> getAllActiveStaff() {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByStatus("ACTIVE");
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getByDepartment(String department) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByDepartment(department);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getByWorkType(String workType) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByWorkType(workType);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getByWorkShift(String workShift) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findByWorkShift(workShift);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public void updatePerformanceScore(Long id, BigDecimal score, String comments) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        existingStaff.setPerformanceScore(score);

        existingStaff.setLastEvaluationDate(LocalDate.now());
        existingStaff.setUpdatedAt(LocalDateTime.now());
        serviceStaffDao.update(existingStaff);
    }

    @Override
    public void recordTraining(Long id, String trainingContent, LocalDate trainingDate) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        // 这里应该记录到培训记录表，暂时更新到主表
        existingStaff.setTrainingRecords(trainingContent);
        existingStaff.setLastTrainingDate(trainingDate);
        existingStaff.setUpdatedAt(LocalDateTime.now());
        serviceStaffDao.update(existingStaff);
    }

    @Override
    public void updateHealthStatus(Long id, String healthStatus, LocalDate examDate) {
        ServiceStaff existingStaff = serviceStaffDao.findById(id);
        if (existingStaff == null) {
            throw new RuntimeException("服务人员不存在，ID：" + id);
        }
        existingStaff.setHealthStatus(healthStatus);
        existingStaff.setLastPhysicalExamDate(examDate);
        existingStaff.setUpdatedAt(LocalDateTime.now());
        serviceStaffDao.update(existingStaff);
    }

    @Override
    public List<ServiceStaffDTO> getExpiringCertifications(int days) {
        // 这里应该实现证书到期查询逻辑
        // 暂时返回空列表
        return List.of();
    }

    @Override
    public List<ServiceStaffDTO> getStaffNeedingPhysicalExam(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findNeedingPhysicalExam(cutoffDate);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getStaffNeedingTraining(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findNeedingTraining(cutoffDate);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> searchBySkills(String skills) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findBySkillsContaining(skills);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getBirthdayReminders(int days) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findUpcomingBirthdays(days);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getWorkAnniversaryReminders(int days) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findUpcomingWorkAnniversaries(days);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public Map<String, Long> getPositionStatistics(Long organizationId) {
        return serviceStaffDao.getPositionStatistics(organizationId);
    }

    @Override
    public Map<String, Long> getDepartmentStatistics(Long organizationId) {
        return serviceStaffDao.getDepartmentStatistics(organizationId);
    }

    @Override
    public Map<String, Long> getAgeDistribution(Long organizationId) {
        return serviceStaffDao.getAgeDistribution(organizationId);
    }

    @Override
    public Map<String, Long> getEducationDistribution(Long organizationId) {
        return serviceStaffDao.getEducationDistribution(organizationId);
    }

    @Override
    public List<ServiceStaffDTO> getTopPerformers(Long organizationId, int limit) {
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findTopPerformers(organizationId, limit);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getNewEmployees(Long organizationId, int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findNewEmployees(organizationId, cutoffDate);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }

    @Override
    public List<ServiceStaffDTO> getSeniorEmployees(Long organizationId, int years) {
        LocalDate cutoffDate = LocalDate.now().minusYears(years);
        List<ServiceStaff> serviceStaffs = serviceStaffDao.findSeniorEmployees(organizationId, cutoffDate);
        return ServiceStaffDTOMapper.INSTANCE.toDTO(serviceStaffs);
    }
}