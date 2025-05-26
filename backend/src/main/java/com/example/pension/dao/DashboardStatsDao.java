package com.example.pension.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页统计数据DAO接口
 */
@Mapper
public interface DashboardStatsDao {
    
    // ===== 老龄人口统计 =====
    /**
     * 获取老人总数
     */
    Long countTotalElderly();
    
    /**
     * 获取80岁以上老人数量
     */
    Long countElderlyOver80();
    
    /**
     * 获取独居老人数量（根据养老类型或特殊标记）
     */
    Long countLivingAloneElderly();
    
    /**
     * 获取失能老人数量（根据能力评估）
     */
    Long countDisabledElderly();
    
    /**
     * 获取低收入老人数量（可以根据备注或特殊字段）
     */
    Long countLowIncomeElderly();
    
    /**
     * 根据老人类型统计数量
     */
    Long countElderlyByType(@Param("elderlyType") String elderlyType);
    
    /**
     * 获取所有老人类型的统计数据
     */
    List<Map<String, Object>> getElderlyTypeStatistics();
    
    // ===== 机构统计 =====
    /**
     * 获取机构总数
     */
    Long countTotalOrganizations();
    
    /**
     * 根据机构类型统计数量
     */
    Long countOrganizationsByType(@Param("type") String type);
    
    // ===== 从业人员统计 =====
    /**
     * 获取从业人员总数（所有机构的员工总数）
     */
    Long countTotalStaff();
    
    /**
     * 获取护理人员总数（所有机构的专业护理人员总数）
     */
    Long countTotalNurses();
    
    /**
     * 获取医生数量（估算或从用户表中统计）
     */
    Long countDoctors();
    
    /**
     * 获取社工数量（估算）
     */
    Long countSocialWorkers();
    
    // ===== 老人类型统计 =====
    /**
     * 根据能力评估统计老人数量
     */
    Long countElderlyByAbilityAssessment(@Param("assessment") String assessment);
    
    /**
     * 根据养老类型统计老人数量
     */
    Long countElderlyByPensionType(@Param("pensionType") String pensionType);
    
    // ===== 年龄分布统计 =====
    /**
     * 统计各年龄段老人数量
     */
    Long countElderlyByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    // ===== 设备统计 =====
    /**
     * 根据设备类型统计数量
     */
    Long countDevicesByType(@Param("deviceType") String deviceType);
    
    /**
     * 根据设备状态统计数量
     */
    Long countDevicesByStatus(@Param("deviceStatus") String deviceStatus);
    
    /**
     * 获取设备总数
     */
    Long countTotalDevices();
    
    // ===== 告警统计 =====
    /**
     * 获取今日告警数量
     */
    Long countTodayAlarms();
    
    /**
     * 获取本周告警数量
     */
    Long countWeekAlarms();
    
    /**
     * 获取本月告警数量
     */
    Long countMonthAlarms();
    
    /**
     * 获取未处理告警数量
     */
    Long countUnhandledAlarms();
    
    // ===== 地图数据 =====
    /**
     * 获取社区统计数据
     */
    List<Map<String, Object>> getCommunityStats();
    
    /**
     * 获取最近告警数据
     */
    List<Map<String, Object>> getRecentAlarms(@Param("limit") Integer limit);
} 