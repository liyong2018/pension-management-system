package com.example.pension.dao;

import com.example.pension.model.FaceStrangerRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 陌生人抓拍记录数据访问接口
 */
@Mapper
public interface FaceStrangerRecordDao {
    
    /**
     * 插入陌生人抓拍记录
     * @param record 抓拍记录
     * @return 影响行数
     */
    @Insert("INSERT INTO face_stranger_record (snap_id, snap_time, image_path, device_id, organization_id, created_at, updated_at) " +
            "VALUES (#{snapId}, #{snapTime}, #{imagePath}, #{deviceId}, #{organizationId}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FaceStrangerRecord record);
    
    /**
     * 根据ID查询抓拍记录
     * @param id 记录ID
     * @return 抓拍记录
     */
    @Select("SELECT id, snap_id, snap_time, image_path, device_id, organization_id, created_at, updated_at " +
            "FROM face_stranger_record WHERE id = #{id}")
    FaceStrangerRecord findById(@Param("id") Long id);
    
    /**
     * 查询所有抓拍记录
     * @return 抓拍记录列表
     */
    @Select("SELECT id, snap_id, snap_time, image_path, device_id, organization_id, created_at, updated_at " +
            "FROM face_stranger_record ORDER BY snap_time DESC")
    List<FaceStrangerRecord> findAll();
    
    /**
     * 搜索抓拍记录
     * @param deviceId 设备ID
     * @param processStatus 处理状态
     * @param organizationId 机构ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> search(
            @Param("deviceId") String deviceId,
            @Param("processStatus") String processStatus,
            @Param("organizationId") Long organizationId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
    
    /**
     * 根据设备ID查询最近的抓拍记录
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> findRecentByDevice(
            @Param("deviceId") String deviceId,
            @Param("limit") int limit
    );
    
    /**
     * 查询待处理的抓拍记录
     * @param organizationId 机构ID（可选）
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> findPendingRecords(@Param("organizationId") Long organizationId);
    
    /**
     * 根据时间范围统计抓拍记录数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录数量
     */
    int countByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 根据处理状态统计抓拍记录数量
     * @param processStatus 处理状态
     * @return 记录数量
     */
    int countByProcessStatus(@Param("processStatus") String processStatus);
    
    /**
     * 根据机构ID统计抓拍记录数量
     * @param organizationId 机构ID
     * @return 记录数量
     */
    int countByOrganization(@Param("organizationId") Long organizationId);
    
    /**
     * 根据设备ID统计抓拍记录数量
     * @param deviceId 设备ID
     * @return 记录数量
     */
    int countByDevice(@Param("deviceId") String deviceId);
    
    /**
     * 更新抓拍记录
     * @param record 抓拍记录
     * @return 影响行数
     */
    int update(FaceStrangerRecord record);
    
    /**
     * 根据ID删除抓拍记录
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据snapId查询抓拍记录（防重复）
     * @param snapId 抓拍ID
     * @return 抓拍记录
     */
    FaceStrangerRecord findBySnapId(@Param("snapId") Integer snapId);
    
    /**
     * 根据机构ID查询抓拍记录
     * @param organizationId 机构ID
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> findByOrganization(@Param("organizationId") Long organizationId);
    
    /**
     * 根据设备ID查询抓拍记录
     * @param deviceId 设备ID
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> findByDevice(@Param("deviceId") String deviceId);
    
    /**
     * 根据处理状态查询抓拍记录
     * @param processStatus 处理状态
     * @return 抓拍记录列表
     */
    List<FaceStrangerRecord> findByProcessStatus(@Param("processStatus") String processStatus);
    
    /**
     * 批量更新处理状态
     * @param ids 记录ID列表
     * @param processStatus 处理状态
     * @param processedBy 处理人ID
     * @return 影响行数
     */
    int batchUpdateProcessStatus(
            @Param("ids") List<Long> ids,
            @Param("processStatus") String processStatus,
            @Param("processedBy") Long processedBy
    );
}