package com.example.pension.service;

import com.example.pension.dto.FaceRecognitionRecordDTO;
import com.example.pension.dto.FaceStrangerRecordDTO;
import com.example.pension.model.face.RecInfo;
import com.example.pension.model.face.StrangerInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 人脸识别服务接口
 * 处理人脸识别相关的业务逻辑
 */
public interface FaceRecognitionService {
    
    /**
     * 处理认证记录推送数据
     * @param recInfo 认证记录信息
     */
    void processRecognitionRecord(RecInfo recInfo);
    
    /**
     * 处理陌生人抓拍推送数据
     * @param strangerInfo 陌生人抓拍信息
     */
    void processStrangerRecord(StrangerInfo strangerInfo);
    
    /**
     * 获取认证记录列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageInfo<FaceRecognitionRecordDTO> getRecognitionRecords(int pageNum, int pageSize);
    
    /**
     * 搜索认证记录
     * @param customId 人员ID
     * @param deviceId 设备ID
     * @param verifyStatus 认证状态
     * @param elderlyId 老人ID
     * @param organizationId 机构ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageInfo<FaceRecognitionRecordDTO> searchRecognitionRecords(
            String customId, String deviceId, Integer verifyStatus,
            Long elderlyId, Long organizationId, String startTime, String endTime,
            int pageNum, int pageSize);
    
    /**
     * 获取陌生人抓拍记录列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageInfo<FaceStrangerRecordDTO> getStrangerRecords(int pageNum, int pageSize);
    
    /**
     * 搜索陌生人抓拍记录
     * @param deviceId 设备ID
     * @param processStatus 处理状态
     * @param organizationId 机构ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageInfo<FaceStrangerRecordDTO> searchStrangerRecords(
            String deviceId, String processStatus, Long organizationId,
            String startTime, String endTime, int pageNum, int pageSize);
    
    /**
     * 处理陌生人抓拍记录
     * @param id 记录ID
     * @param processResult 处理结果
     * @param processedBy 处理人ID
     * @param remarks 备注
     */
    void processStrangerRecord(Long id, String processResult, Long processedBy, String remarks);
    
    /**
     * 获取认证统计数据
     * @return 统计数据
     */
    Map<String, Object> getRecognitionStatistics();
    
    /**
     * 获取陌生人抓拍统计数据
     * @return 统计数据
     */
    Map<String, Object> getStrangerStatistics();
    
    /**
     * 根据设备ID获取最近的认证记录
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 认证记录列表
     */
    List<FaceRecognitionRecordDTO> getRecentRecognitionsByDevice(String deviceId, int limit);
    
    /**
     * 根据老人ID获取最近的认证记录
     * @param elderlyId 老人ID
     * @param limit 限制数量
     * @return 认证记录列表
     */
    List<FaceRecognitionRecordDTO> getRecentRecognitionsByElderly(Long elderlyId, int limit);
    
    /**
     * 获取待处理的陌生人抓拍记录
     * @param organizationId 机构ID（可选）
     * @return 待处理记录列表
     */
    List<FaceStrangerRecordDTO> getPendingStrangerRecords(Long organizationId);
}