package com.example.pension.dao;

import com.example.pension.model.WatchDataRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 手环数据记录DAO接口
 */
@Mapper
public interface WatchDataRecordDao {
    
    /**
     * 插入手环数据记录
     * 
     * @param record 手环数据记录
     * @return 影响行数
     */
    int insert(WatchDataRecord record);
    
    /**
     * 根据ID查询手环数据记录
     * 
     * @param id 记录ID
     * @return 手环数据记录
     */
    WatchDataRecord findById(@Param("id") Long id);
    
    /**
     * 根据设备ID查询最新的手环数据记录
     * 
     * @param deviceId 设备ID
     * @return 手环数据记录
     */
    WatchDataRecord findLatestByDeviceId(@Param("deviceId") String deviceId);
    
    /**
     * 根据老人ID查询最新的手环数据记录
     * 
     * @param elderlyId 老人ID
     * @return 手环数据记录
     */
    WatchDataRecord findLatestByElderlyId(@Param("elderlyId") Long elderlyId);
    
    /**
     * 根据设备ID查询手环数据记录列表
     * 
     * @param deviceId 设备ID
     * @param limit 限制条数
     * @return 手环数据记录列表
     */
    List<WatchDataRecord> findByDeviceId(@Param("deviceId") String deviceId, @Param("limit") int limit);
    
    /**
     * 根据老人ID查询手环数据记录列表
     * 
     * @param elderlyId 老人ID
     * @param limit 限制条数
     * @return 手环数据记录列表
     */
    List<WatchDataRecord> findByElderlyId(@Param("elderlyId") Long elderlyId, @Param("limit") int limit);
    
    /**
     * 根据条件查询手环数据记录列表
     * 
     * @param deviceId 设备ID
     * @param elderlyId 老人ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 手环数据记录列表
     */
    List<WatchDataRecord> findWithConditions(
            @Param("deviceId") String deviceId,
            @Param("elderlyId") Long elderlyId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("offset") int offset,
            @Param("limit") int limit);
    
    /**
     * 更新手环数据记录
     * 
     * @param record 手环数据记录
     * @return 影响行数
     */
    int update(WatchDataRecord record);
    
    /**
     * 删除手环数据记录
     * 
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}