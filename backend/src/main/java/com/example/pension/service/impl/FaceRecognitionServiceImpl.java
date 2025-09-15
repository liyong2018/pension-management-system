package com.example.pension.service.impl;

import com.example.pension.dao.FaceRecognitionRecordDao;
import com.example.pension.dao.FaceStrangerRecordDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.SmartDeviceDao;
import com.example.pension.dto.FaceRecognitionRecordDTO;
import com.example.pension.dto.FaceStrangerRecordDTO;
import com.example.pension.model.FaceRecognitionRecord;
import com.example.pension.model.FaceStrangerRecord;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.SmartDevice;
import com.example.pension.model.Organization;
import com.example.pension.model.face.RecInfo;
import com.example.pension.model.face.StrangerInfo;
import com.example.pension.service.FaceRecognitionService;
import com.example.pension.util.ImageUtils;
import com.example.pension.constant.DeviceTypeConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 人脸识别服务实现类
 */
@Service
@Transactional
public class FaceRecognitionServiceImpl implements FaceRecognitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(FaceRecognitionServiceImpl.class);
    
    @Autowired
    private FaceRecognitionRecordDao faceRecognitionRecordDao;
    
    @Autowired
    private FaceStrangerRecordDao faceStrangerRecordDao;
    
    @Autowired
    private ElderlyProfileDao elderlyProfileDao;
    
    @Autowired
    private SmartDeviceDao smartDeviceDao;
    
    @Autowired
    private ImageUtils imageUtils;
    
    @Override
    public void processRecognitionRecord(RecInfo recInfo) {
        try {
            logger.info("开始处理认证记录: customId={}, recordId={}", recInfo.getCustomId(), recInfo.getRecordID());
            
            // 创建认证记录实体
            FaceRecognitionRecord record = new FaceRecognitionRecord();
            record.setCustomId(recInfo.getCustomId());
            record.setPersonId(recInfo.getPersonId());
            record.setRecordId(recInfo.getRecordID() != null ? recInfo.getRecordID().intValue() : null);
            record.setVerifyStatus(recInfo.getVerifyStatus());
            record.setPersonType(recInfo.getPersonType());
            record.setSimilarity(recInfo.getSimilarity1());
            record.setSimilarity2(recInfo.getSimilarity2());
            record.setSendintime(recInfo.getSendintime());
            record.setDirection(recInfo.getDirection() != null ? recInfo.getDirection().toString() : null);
            record.setOtype(recInfo.getOtype() != null ? recInfo.getOtype().toString() : null);
            record.setPersionName(recInfo.getPersionName());
            record.setPersonName(recInfo.getPersonName());
            record.setName(recInfo.getName());
            
            // 添加调试日志
            logger.info("RecInfo字段值 - Name: {}, PersionName: {}, PersonName: {}", 
                recInfo.getName(), recInfo.getPersionName(), recInfo.getPersonName());
            record.setFacesluiceName(recInfo.getFacesluiceName());
            record.setIdCard(recInfo.getIdCard());
            record.setTelnum(recInfo.getTelnum());
            record.setLeft(recInfo.getLeft());
            record.setTop(recInfo.getTop());
            record.setRight(recInfo.getRight());
            record.setBottom(recInfo.getBottom());
            record.setPushType(recInfo.getPushType());
            record.setOpendoorWay(recInfo.getOpendoorWay());
            record.setCardNum2(recInfo.getCardNum2());
            record.setRfidCard(recInfo.getRfidCard());
            record.setSzQrCodeData(recInfo.getSzQrCodeData());
            record.setIsNoMask(recInfo.getIsNoMask() != null ? recInfo.getIsNoMask().toString() : null);
            record.setDwFileIndex(recInfo.getDwFileIndex());
            record.setDwFilePos(recInfo.getDwFilePos());
            record.setTemperature(recInfo.getTemperature());
            record.setDeviceId(recInfo.getFacesluiceId());
            
            // 添加缺失的字段映射
            if (recInfo.getGender() != null) {
                record.setGender(recInfo.getGender().toString());
            }
            if (recInfo.getNation() != null) {
                record.setNation(recInfo.getNation().toString());
            }
            if (recInfo.getCardType() != null) {
                record.setCardType(recInfo.getCardType().toString());
            }
            record.setBirthday(recInfo.getBirthday());
            record.setNativePlace(recInfo.getNativePlace());
            record.setAddress(recInfo.getAddress());
            record.setNotes(recInfo.getNotes());
            record.setMjCardFrom(recInfo.getMjCardFrom());
            record.setMjCardNo(recInfo.getMjCardNo());
            record.setTempValid(recInfo.getTempvalid() != null ? recInfo.getTempvalid().toString() : null);
            record.setValidBegin(recInfo.getValidBegin());
            record.setValidEnd(recInfo.getValidEnd());
            record.setPersonUuid(recInfo.getPersonUUID());
            
            // 解析识别时间
            if (StringUtils.hasText(recInfo.getTime())) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    record.setRecognitionTime(LocalDateTime.parse(recInfo.getTime(), formatter));
                } catch (Exception e) {
                    logger.warn("解析识别时间失败: {}", recInfo.getTime(), e);
                    record.setRecognitionTime(LocalDateTime.now());
                }
            } else {
                record.setRecognitionTime(LocalDateTime.now());
            }
            
            // 处理Base64图片 - 优先处理SanpPic字段
            logger.info("检查图片字段 - SanpPic: {}, Pic: {}", 
                StringUtils.hasText(recInfo.getSanpPic()) ? "有数据" : "无数据",
                StringUtils.hasText(recInfo.getPic()) ? "有数据" : "无数据");
            
            String base64Image = null;
            if (StringUtils.hasText(recInfo.getSanpPic())) {
                base64Image = recInfo.getSanpPic();
                logger.info("使用SanpPic字段，数据长度: {}", base64Image.length());
            } else if (StringUtils.hasText(recInfo.getPic())) {
                base64Image = recInfo.getPic();
                logger.info("使用Pic字段，数据长度: {}", base64Image.length());
            }
            
            if (StringUtils.hasText(base64Image)) {
                String imagePath = imageUtils.saveBase64Image(
                    base64Image, 
                    "face_recognition", 
                    "verify_" + (recInfo.getRecordID() != null ? recInfo.getRecordID() : System.currentTimeMillis()) + "_" + System.currentTimeMillis()
                );
                record.setImagePath(imagePath);
                logger.info("人脸识别图片已保存: {}", imagePath);
            } else {
                logger.warn("未找到有效的图片数据");
            }
            
            // 根据customId查找或创建对应的老人信息
            if (StringUtils.hasText(recInfo.getCustomId())) {
                ElderlyProfile elderly = elderlyProfileDao.findByCustomId(recInfo.getCustomId());
                if (elderly != null) {
                    // 更新现有记录的个人信息（如果RecInfo中有新数据）
                    boolean needUpdate = false;
                    if (StringUtils.hasText(recInfo.getName()) && !recInfo.getName().equals(elderly.getName())) {
                        elderly.setName(recInfo.getName());
                        needUpdate = true;
                    }
                    if (recInfo.getGender() != null) {
                        String genderStr = recInfo.getGender() == 0 ? "男" : "女";
                        if (!genderStr.equals(elderly.getGender())) {
                            elderly.setGender(genderStr);
                            needUpdate = true;
                        }
                    }
                    if (StringUtils.hasText(recInfo.getBirthday())) {
                        try {
                            // 解析生日字符串为日期
                            java.time.LocalDate birthDate = java.time.LocalDate.parse(recInfo.getBirthday());
                            if (!birthDate.equals(elderly.getBirthDate())) {
                                elderly.setBirthDate(birthDate);
                                needUpdate = true;
                            }
                        } catch (Exception e) {
                            logger.warn("解析生日失败: {}", recInfo.getBirthday(), e);
                        }
                    }
                    if (StringUtils.hasText(recInfo.getIdCard()) && !recInfo.getIdCard().equals(elderly.getIdCardNumber())) {
                        elderly.setIdCardNumber(recInfo.getIdCard());
                        needUpdate = true;
                    }
                    if (StringUtils.hasText(recInfo.getTelnum()) && !recInfo.getTelnum().equals(elderly.getPhone())) {
                        elderly.setPhone(recInfo.getTelnum());
                        needUpdate = true;
                    }
                    
                    if (needUpdate) {
                        elderly.setUpdateTime(LocalDateTime.now());
                        elderlyProfileDao.update(elderly);
                        logger.info("更新老人档案信息: customId={}, name={}", recInfo.getCustomId(), recInfo.getName());
                    }
                    
                    record.setElderlyId(elderly.getId());
                    record.setOrganizationId(elderly.getOrganizationId());
                } else if (StringUtils.hasText(recInfo.getName())) {
                    // 创建新的老人档案记录
                    elderly = new ElderlyProfile();
                    elderly.setCustomId(recInfo.getCustomId());
                    elderly.setName(recInfo.getName());
                    
                    if (recInfo.getGender() != null) {
                        elderly.setGender(recInfo.getGender() == 0 ? "男" : "女");
                    }
                    
                    if (StringUtils.hasText(recInfo.getBirthday())) {
                        try {
                            elderly.setBirthDate(java.time.LocalDate.parse(recInfo.getBirthday()));
                        } catch (Exception e) {
                            logger.warn("解析生日失败: {}", recInfo.getBirthday(), e);
                        }
                    }
                    
                    if (StringUtils.hasText(recInfo.getIdCard())) {
                        elderly.setIdCardNumber(recInfo.getIdCard());
                    }
                    
                    if (StringUtils.hasText(recInfo.getTelnum())) {
                        elderly.setPhone(recInfo.getTelnum());
                    }
                    
                    elderly.setCreateTime(LocalDateTime.now());
                    elderly.setUpdateTime(LocalDateTime.now());
                    
                    // 保存新的老人档案
                    elderlyProfileDao.insert(elderly);
                    logger.info("创建新的老人档案: customId={}, name={}", recInfo.getCustomId(), recInfo.getName());
                    
                    record.setElderlyId(elderly.getId());
                    record.setOrganizationId(elderly.getOrganizationId());
                }
            }
            
            // 处理设备信息
            if (StringUtils.hasText(recInfo.getFacesluiceId())) {
                processDeviceInfo(recInfo.getFacesluiceId(), record.getOrganizationId());
            }
            
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            faceRecognitionRecordDao.insert(record);
            
            logger.info("认证记录处理完成: id={}", record.getId());
            
        } catch (Exception e) {
            logger.error("处理认证记录失败", e);
            throw new RuntimeException("处理认证记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void processStrangerRecord(StrangerInfo strangerInfo) {
        try {
            logger.info("开始处理陌生人抓拍记录: snapId={}", strangerInfo.getSnapID());
            
            // 创建陌生人抓拍记录实体
            FaceStrangerRecord record = new FaceStrangerRecord();
            record.setSnapId(strangerInfo.getSnapID());
            record.setDeviceId(strangerInfo.getFacesluiceId());
            
            // 解析抓拍时间
            if (StringUtils.hasText(strangerInfo.getTime())) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    record.setSnapTime(LocalDateTime.parse(strangerInfo.getTime(), formatter));
                } catch (Exception e) {
                    logger.warn("解析抓拍时间失败: {}", strangerInfo.getTime(), e);
                    record.setSnapTime(LocalDateTime.now());
                }
            } else {
                record.setSnapTime(LocalDateTime.now());
            }
            
            // 处理Base64图片
            if (StringUtils.hasText(strangerInfo.getPic())) {
                String imagePath = imageUtils.saveBase64Image(
                    strangerInfo.getPic(), 
                    "face_stranger", 
                    "stranger_" + strangerInfo.getSnapID() + "_" + System.currentTimeMillis()
                );
                record.setImagePath(imagePath);
            }
            // 处理设备信息
            if (StringUtils.hasText(strangerInfo.getFacesluiceId())) {
                processDeviceInfo(strangerInfo.getFacesluiceId(), null);
            }
            
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            faceStrangerRecordDao.insert(record);
            
            logger.info("陌生人抓拍记录处理完成: id={}", record.getId());
            
        } catch (Exception e) {
            logger.error("处理陌生人抓拍记录失败", e);
            throw new RuntimeException("处理陌生人抓拍记录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PageInfo<FaceRecognitionRecordDTO> getRecognitionRecords(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FaceRecognitionRecord> records = faceRecognitionRecordDao.findAll();
        PageInfo<FaceRecognitionRecord> pageInfo = new PageInfo<>(records);
        
        List<FaceRecognitionRecordDTO> dtoList = records.stream()
                .map(this::convertToRecognitionDTO)
                .collect(Collectors.toList());
        
        PageInfo<FaceRecognitionRecordDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(dtoList);
        
        return result;
    }
    
    @Override
    public PageInfo<FaceRecognitionRecordDTO> searchRecognitionRecords(
            String customId, String deviceId, Integer verifyStatus,
            Long elderlyId, Long organizationId, String startTime, String endTime,
            int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize);
        List<FaceRecognitionRecord> records = faceRecognitionRecordDao.search(
                customId, deviceId, verifyStatus, elderlyId, organizationId, startTime, endTime);
        PageInfo<FaceRecognitionRecord> pageInfo = new PageInfo<>(records);
        
        List<FaceRecognitionRecordDTO> dtoList = records.stream()
                .map(this::convertToRecognitionDTO)
                .collect(Collectors.toList());
        
        PageInfo<FaceRecognitionRecordDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(dtoList);
        
        return result;
    }
    
    @Override
    public PageInfo<FaceStrangerRecordDTO> getStrangerRecords(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FaceStrangerRecord> records = faceStrangerRecordDao.findAll();
        PageInfo<FaceStrangerRecord> pageInfo = new PageInfo<>(records);
        
        List<FaceStrangerRecordDTO> dtoList = records.stream()
                .map(this::convertToStrangerDTO)
                .collect(Collectors.toList());
        
        PageInfo<FaceStrangerRecordDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(dtoList);
        
        return result;
    }
    
    @Override
    public PageInfo<FaceStrangerRecordDTO> searchStrangerRecords(
            String deviceId, String processStatus, Long organizationId,
            String startTime, String endTime, int pageNum, int pageSize) {
        
        PageHelper.startPage(pageNum, pageSize);
        List<FaceStrangerRecord> records = faceStrangerRecordDao.search(
                deviceId, processStatus, organizationId, startTime, endTime);
        PageInfo<FaceStrangerRecord> pageInfo = new PageInfo<>(records);
        
        List<FaceStrangerRecordDTO> dtoList = records.stream()
                .map(this::convertToStrangerDTO)
                .collect(Collectors.toList());
        
        PageInfo<FaceStrangerRecordDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(dtoList);
        
        return result;
    }
    
    @Override
    public void processStrangerRecord(Long id, String processResult, Long processedBy, String remarks) {
        FaceStrangerRecord record = faceStrangerRecordDao.findById(id);
        if (record == null) {
            throw new RuntimeException("陌生人抓拍记录不存在: " + id);
        }
        
        record.setProcessStatus("PROCESSED");
        record.setProcessResult(processResult);
        record.setProcessedBy(processedBy);
        record.setProcessedAt(LocalDateTime.now());
        record.setRemarks(remarks);
        record.setUpdatedAt(LocalDateTime.now());
        
        faceStrangerRecordDao.update(record);
    }
    
    @Override
    public Map<String, Object> getRecognitionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日认证总数
        int todayTotal = faceRecognitionRecordDao.countByDateRange(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
        stats.put("todayTotal", todayTotal);
        
        // 今日成功认证数
        int todaySuccess = faceRecognitionRecordDao.countByDateRangeAndStatus(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay(), 1);
        stats.put("todaySuccess", todaySuccess);
        
        // 今日失败认证数
        int todayFailed = faceRecognitionRecordDao.countByDateRangeAndStatus(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay(), 2);
        stats.put("todayFailed", todayFailed);
        
        // 本月认证总数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        int monthTotal = faceRecognitionRecordDao.countByDateRange(monthStart, LocalDateTime.now());
        stats.put("monthTotal", monthTotal);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getStrangerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日陌生人抓拍总数
        int todayTotal = faceStrangerRecordDao.countByDateRange(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
        stats.put("todayTotal", todayTotal);
        
        // 待处理数量
        int pendingCount = faceStrangerRecordDao.countByProcessStatus("PENDING");
        stats.put("pendingCount", pendingCount);
        
        // 已处理数量
        int processedCount = faceStrangerRecordDao.countByProcessStatus("PROCESSED");
        stats.put("processedCount", processedCount);
        
        // 本月陌生人抓拍总数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        int monthTotal = faceStrangerRecordDao.countByDateRange(monthStart, LocalDateTime.now());
        stats.put("monthTotal", monthTotal);
        
        return stats;
    }
    
    @Override
    public List<FaceRecognitionRecordDTO> getRecentRecognitionsByDevice(String deviceId, int limit) {
        List<FaceRecognitionRecord> records = faceRecognitionRecordDao.findRecentByDevice(deviceId, limit);
        return records.stream()
                .map(this::convertToRecognitionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FaceRecognitionRecordDTO> getRecentRecognitionsByElderly(Long elderlyId, int limit) {
        List<FaceRecognitionRecord> records = faceRecognitionRecordDao.findRecentByElderly(elderlyId, limit);
        return records.stream()
                .map(this::convertToRecognitionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FaceStrangerRecordDTO> getPendingStrangerRecords(Long organizationId) {
        List<FaceStrangerRecord> records = faceStrangerRecordDao.findPendingRecords(organizationId);
        return records.stream()
                .map(this::convertToStrangerDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换认证记录实体为DTO
     */
    private FaceRecognitionRecordDTO convertToRecognitionDTO(FaceRecognitionRecord record) {
        FaceRecognitionRecordDTO dto = new FaceRecognitionRecordDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 设置认证状态描述
        if (record.getVerifyStatus() != null) {
            switch (record.getVerifyStatus()) {
                case 1:
                    dto.setVerifyStatusDesc("允许通行");
                    break;
                case 2:
                    dto.setVerifyStatusDesc("拒绝通行");
                    break;
                case 22:
                    dto.setVerifyStatusDesc("待核验");
                    break;
                default:
                    dto.setVerifyStatusDesc("未知状态");
            }
        }
        
        return dto;
    }
    
    /**
     * 转换陌生人记录实体为DTO
     */
    private FaceStrangerRecordDTO convertToStrangerDTO(FaceStrangerRecord record) {
        FaceStrangerRecordDTO dto = new FaceStrangerRecordDTO();
        BeanUtils.copyProperties(record, dto);
        
        // 设置处理状态描述
        if ("PENDING".equals(record.getProcessStatus())) {
            dto.setProcessStatusDesc("待处理");
        } else if ("PROCESSED".equals(record.getProcessStatus())) {
            dto.setProcessStatusDesc("已处理");
        } else {
            dto.setProcessStatusDesc("未知状态");
        }
        
        return dto;
    }
    
    /**
     * 处理设备信息，如果设备不存在则创建
     * @param deviceId 设备ID
     * @param organizationId 机构ID
     */
    private void processDeviceInfo(String deviceId, Long organizationId) {
        try {
            // 检查设备是否已存在
            SmartDevice existingDevice = smartDeviceDao.findByDeviceCode(deviceId);
            
            if (existingDevice == null) {
                // 创建新设备
                SmartDevice device = new SmartDevice();
                device.setDeviceCode(deviceId);
                device.setDeviceName("人脸识别设备-" + deviceId);
                device.setDeviceType(DeviceTypeConstants.FACE_RECOGNITION);
                device.setDeviceStatus("在线");
                if (organizationId != null) {
                    Organization organization = new Organization();
                    organization.setId(organizationId);
                    device.setOrganization(organization);
                }
                device.setCreateTime(LocalDateTime.now());
                device.setUpdateTime(LocalDateTime.now());
                
                smartDeviceDao.insert(device);
                System.out.println("自动创建人脸识别设备: " + deviceId);
            } else {
                // 更新设备最后活跃时间
                existingDevice.setUpdateTime(LocalDateTime.now());
                existingDevice.setLastCommunicationTime(LocalDateTime.now());
                if (!"在线".equals(existingDevice.getDeviceStatus())) {
                    existingDevice.setDeviceStatus("在线");
                }
                smartDeviceDao.update(existingDevice);
            }
        } catch (Exception e) {
            System.err.println("处理设备信息失败: " + e.getMessage());
        }
    }
}