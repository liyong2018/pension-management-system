package com.example.pension.dao;

import com.example.pension.model.FaceRecognitionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人脸识别记录数据访问接口
 */
@Mapper
public interface FaceRecognitionRecordDao {
    
    /**
     * 插入认证记录
     * @param record 认证记录
     * @return 影响行数
     */
    int insert(FaceRecognitionRecord record);
    
    /**
     * 根据ID查询认证记录
     * @param id 记录ID
     * @return 认证记录
     */
    FaceRecognitionRecord findById(@Param("id") Long id);
    
    /**
     * 查询所有认证记录
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> findAll();
    
    /**
     * 搜索认证记录
     * @param customId 人员ID
     * @param deviceId 设备ID
     * @param verifyStatus 认证状态
     * @param elderlyId 老人ID
     * @param organizationId 机构ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> search(
            @Param("customId") String customId,
            @Param("deviceId") String deviceId,
            @Param("verifyStatus") Integer verifyStatus,
            @Param("elderlyId") Long elderlyId,
            @Param("organizationId") Long organizationId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
    
    /**
     * 根据设备ID查询最近的认证记录
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> findRecentByDevice(
            @Param("deviceId") String deviceId,
            @Param("limit") int limit
    );
    
    /**
     * 根据老人ID查询最近的认证记录
     * @param elderlyId 老人ID
     * @param limit 限制数量
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> findRecentByElderly(
            @Param("elderlyId") Long elderlyId,
            @Param("limit") int limit
    );
    
    /**
     * 根据时间范围统计认证记录数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录数量
     */
    int countByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 根据时间范围和认证状态统计认证记录数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param verifyStatus 认证状态
     * @return 记录数量
     */
    int countByDateRangeAndStatus(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("verifyStatus") Integer verifyStatus
    );
    
    /**
     * 根据机构ID统计认证记录数量
     * @param organizationId 机构ID
     * @return 记录数量
     */
    int countByOrganization(@Param("organizationId") Long organizationId);
    
    /**
     * 根据设备ID统计认证记录数量
     * @param deviceId 设备ID
     * @return 记录数量
     */
    int countByDevice(@Param("deviceId") String deviceId);
    
    /**
     * 根据老人ID统计认证记录数量
     * @param elderlyId 老人ID
     * @return 记录数量
     */
    int countByElderly(@Param("elderlyId") Long elderlyId);
    
    /**
     * 更新认证记录
     * @param record 认证记录
     * @return 影响行数
     */
    int update(FaceRecognitionRecord record);
    
    /**
     * 根据ID删除认证记录
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据customId和recordId查询认证记录（防重复）
     * @param customId 人员ID
     * @param recordId 记录ID
     * @return 认证记录
     */
    FaceRecognitionRecord findByCustomIdAndRecordId(
            @Param("customId") String customId,
            @Param("recordId") Integer recordId
    );
    
    /**
     * 根据机构ID查询认证记录
     * @param organizationId 机构ID
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> findByOrganization(@Param("organizationId") Long organizationId);
    
    /**
     * 根据设备ID查询认证记录
     * @param deviceId 设备ID
     * @return 认证记录列表
     */
    List<FaceRecognitionRecord> findByDevice(@Param("deviceId") String deviceId);
}