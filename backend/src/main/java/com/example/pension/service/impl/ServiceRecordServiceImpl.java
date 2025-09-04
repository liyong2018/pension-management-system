package com.example.pension.service.impl;

import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.ServiceRecordDao;
import com.example.pension.dto.ServiceRecordDTO;
import com.example.pension.exception.ResourceNotFoundException;
import com.example.pension.exception.ValidationException;
import com.example.pension.mapper.ServiceRecordMapper;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.ServiceRecord;
import com.example.pension.service.ServiceRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordDao serviceRecordDao;
    private final ElderlyProfileDao elderlyProfileDao;
    private final ServiceRecordMapper serviceRecordMapper;

    private PageInfo<ServiceRecordDTO> convertToDtoPageInfo(PageInfo<ServiceRecord> entityPageInfo) {
        List<ServiceRecordDTO> dtoList = entityPageInfo.getList().stream()
            .map(serviceRecordMapper::toDTO)
            .collect(Collectors.toList());
        
        PageInfo<ServiceRecordDTO> dtoPageInfo = new PageInfo<>(dtoList);
        dtoPageInfo.setPageNum(entityPageInfo.getPageNum());
        dtoPageInfo.setPageSize(entityPageInfo.getPageSize());
        dtoPageInfo.setTotal(entityPageInfo.getTotal());
        dtoPageInfo.setPages(entityPageInfo.getPages());
        dtoPageInfo.setNavigatePages(entityPageInfo.getNavigatePages());
        dtoPageInfo.setIsFirstPage(entityPageInfo.isIsFirstPage());
        dtoPageInfo.setIsLastPage(entityPageInfo.isIsLastPage());
        dtoPageInfo.setHasPreviousPage(entityPageInfo.isHasPreviousPage());
        dtoPageInfo.setHasNextPage(entityPageInfo.isHasNextPage());
        dtoPageInfo.setNavigatepageNums(entityPageInfo.getNavigatepageNums());
        dtoPageInfo.setNavigateFirstPage(entityPageInfo.getNavigateFirstPage());
        dtoPageInfo.setNavigateLastPage(entityPageInfo.getNavigateLastPage());
        
        return dtoPageInfo;
    }

    @Override
    @Transactional
    public ServiceRecordDTO create(ServiceRecordDTO serviceRecordDTO) {
        // 验证老人ID是否存在
        ElderlyProfile elderlyProfile = elderlyProfileDao.findById(serviceRecordDTO.getElderlyId());
        if (elderlyProfile == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + serviceRecordDTO.getElderlyId());
        }

        ServiceRecord serviceRecord = serviceRecordMapper.toEntity(serviceRecordDTO);
        serviceRecord.setElderlyProfile(elderlyProfile);
        
        // 设置默认状态为"待处理"
        if (serviceRecord.getStatus() == null || serviceRecord.getStatus().isEmpty()) {
            serviceRecord.setStatus("待处理");
        }

        serviceRecordDao.insert(serviceRecord);
        
        return serviceRecordMapper.toDTO(serviceRecordDao.findById(serviceRecord.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRecordDTO getById(Long id) {
        ServiceRecord serviceRecord = serviceRecordDao.findById(id);
        if (serviceRecord == null) {
            throw new ResourceNotFoundException("服务记录不存在: " + id);
        }
        return serviceRecordMapper.toDTO(serviceRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<ServiceRecordDTO> getAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ServiceRecord> serviceRecords = serviceRecordDao.findWithConditions(
                null, null, null, null, null, null, 
                null, null, 0, 0);
        PageInfo<ServiceRecord> entityPageInfo = new PageInfo<>(serviceRecords);
        return convertToDtoPageInfo(entityPageInfo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageInfo<ServiceRecordDTO> searchByKeyword(
            String keyword,
            String serviceProviderType,
            Long serviceProviderId,
            String status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int pageNum,
            int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ServiceRecord> serviceRecords = serviceRecordDao.findWithKeywordSearch(
                StringUtils.hasText(keyword) ? keyword : null,
                StringUtils.hasText(serviceProviderType) ? serviceProviderType : null,
                serviceProviderId,
                StringUtils.hasText(status) ? status : null,
                startTime,
                endTime,
                0,
                0);
        PageInfo<ServiceRecord> entityPageInfo = new PageInfo<>(serviceRecords);
        return convertToDtoPageInfo(entityPageInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<ServiceRecordDTO> searchByMultipleConditions(
            String elderlyName,
            String serviceContent,
            String serviceProviderName,
            String serviceProviderType,
            Long serviceProviderId,
            String status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int pageNum,
            int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ServiceRecord> serviceRecords = serviceRecordDao.findWithConditions(
                StringUtils.hasText(elderlyName) ? elderlyName : null,
                StringUtils.hasText(serviceContent) ? serviceContent : null,
                StringUtils.hasText(serviceProviderName) ? serviceProviderName : null,
                StringUtils.hasText(serviceProviderType) ? serviceProviderType : null,
                serviceProviderId,
                StringUtils.hasText(status) ? status : null,
                startTime,
                endTime,
                0,
                0);
        PageInfo<ServiceRecord> entityPageInfo = new PageInfo<>(serviceRecords);
        return convertToDtoPageInfo(entityPageInfo);
    }

    @Override
    @Transactional
    public ServiceRecordDTO update(Long id, ServiceRecordDTO serviceRecordDTO) {
        ServiceRecord serviceRecord = serviceRecordDao.findById(id);
        if (serviceRecord == null) {
            throw new ResourceNotFoundException("服务记录不存在: " + id);
        }

        // 如果老人ID发生变化，验证新的老人ID
        if (serviceRecordDTO.getElderlyId() != null && 
            !serviceRecordDTO.getElderlyId().equals(serviceRecord.getElderlyId())) {
            ElderlyProfile elderlyProfile = elderlyProfileDao.findById(serviceRecordDTO.getElderlyId());
            if (elderlyProfile == null) {
                throw new ResourceNotFoundException("老人档案不存在: " + serviceRecordDTO.getElderlyId());
            }
            serviceRecord.setElderlyProfile(elderlyProfile);
        }

        serviceRecordMapper.updateEntityFromDTO(serviceRecordDTO, serviceRecord);
        serviceRecordDao.update(serviceRecord);
        
        return serviceRecordMapper.toDTO(serviceRecordDao.findById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (serviceRecordDao.findById(id) == null) {
            throw new ResourceNotFoundException("服务记录不存在: " + id);
        }
        serviceRecordDao.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("删除列表不能为空");
        }
        serviceRecordDao.deleteBatchByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRecordDTO> getByElderlyId(Long elderlyId) {
        if (elderlyProfileDao.findById(elderlyId) == null) {
            throw new ResourceNotFoundException("老人档案不存在: " + elderlyId);
        }
        List<ServiceRecord> serviceRecords = serviceRecordDao.findAllByElderlyId(elderlyId);
        return serviceRecordMapper.toDTO(serviceRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStatusStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        String[] statuses = {"待处理", "进行中", "已完成", "已评价"};
        
        for (String status : statuses) {
            long count = serviceRecordDao.countByStatus(status);
            statistics.put(status, count);
        }
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getServiceProviderTypeStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        String[] types = {"机构员工", "志愿者", "外包服务", "家政服务"};
        
        for (String type : types) {
            long count = serviceRecordDao.countByServiceProviderType(type);
            statistics.put(type, count);
        }
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRecordDTO> getRecordsForEvaluation() {
        List<ServiceRecord> serviceRecords = serviceRecordDao.findByStatusForEvaluation("已完成");
        return serviceRecordMapper.toDTO(serviceRecords);
    }

    @Override
    @Transactional
    public ServiceRecordDTO evaluate(Long id, Integer score, String comment) {
        ServiceRecord serviceRecord = serviceRecordDao.findById(id);
        if (serviceRecord == null) {
            throw new ResourceNotFoundException("服务记录不存在: " + id);
        }
        
        if (!"已完成".equals(serviceRecord.getStatus())) {
            throw new ValidationException("只有已完成的服务记录才能进行评价");
        }
        
        if (score == null || score < 1 || score > 5) {
            throw new ValidationException("评价分数必须在1-5之间");
        }
        
        serviceRecord.setEvaluationScore(score);
        serviceRecord.setEvaluationComment(comment);
        serviceRecord.setStatus("已评价");
        
        serviceRecordDao.update(serviceRecord);
        
        return serviceRecordMapper.toDTO(serviceRecord);
    }
}