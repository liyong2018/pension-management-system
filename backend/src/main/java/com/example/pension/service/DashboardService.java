package com.example.pension.service;

import com.example.pension.dao.DashboardStatsDao;
import com.example.pension.dto.DashboardStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 首页数据服务
 */
@Service
public class DashboardService {
    
    @Autowired
    private DashboardStatsDao dashboardStatsDao;
    
    /**
     * 获取首页统计数据
     */
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        try {
            // 老龄人口统计
            Long totalElderly = dashboardStatsDao.countTotalElderly();
            Long over80Count = dashboardStatsDao.countElderlyOver80();
            Long livingAloneCount = dashboardStatsDao.countLivingAloneElderly();
            Long disabledCount = dashboardStatsDao.countDisabledElderly();
            Long lowIncomeCount = dashboardStatsDao.countLowIncomeElderly();
            
            stats.setElderlyStats(new DashboardStatsDTO.ElderlyStatsDTO(
                totalElderly != null ? totalElderly : 0L,
                over80Count != null ? over80Count : 0L,
                livingAloneCount != null ? livingAloneCount : 0L,
                disabledCount != null ? disabledCount : 0L,
                lowIncomeCount != null ? lowIncomeCount : 0L
            ));
            
            // 养老机构及设施统计
            Long totalOrgs = dashboardStatsDao.countTotalOrganizations();
            Long homeCareCount = dashboardStatsDao.countOrganizationsByType("居家养老单位");
            Long dayCareCount = dashboardStatsDao.countOrganizationsByType("社区养老单位（日照）");
            Long institutionCount = dashboardStatsDao.countOrganizationsByType("机构养老单位（养老院）");
            
            stats.setFacilityStats(new DashboardStatsDTO.FacilityStatsDTO(
                totalOrgs != null ? totalOrgs : 0L,
                homeCareCount != null ? homeCareCount : 0L,
                dayCareCount != null ? dayCareCount : 0L,
                institutionCount != null ? institutionCount : 0L
            ));
            
            // 从业人员统计
            Long totalStaff = dashboardStatsDao.countTotalStaff();
            Long nurseCount = dashboardStatsDao.countTotalNurses();
            Long doctorCount = dashboardStatsDao.countDoctors();
            Long socialWorkerCount = dashboardStatsDao.countSocialWorkers();
            
            stats.setStaffStats(new DashboardStatsDTO.StaffStatsDTO(
                totalStaff != null ? totalStaff : 0L,
                nurseCount != null ? nurseCount : 0L,
                doctorCount != null ? doctorCount : 0L,
                socialWorkerCount != null ? socialWorkerCount : 0L
            ));
            
            // 发放补贴统计（暂时使用模拟数据，因为数据库中没有相关表）
            stats.setSubsidyStats(new DashboardStatsDTO.SubsidyStatsDTO(
                2580000.0,  // 总金额
                totalElderly != null ? (long)(totalElderly * 0.7) : 0L,  // 受益人数（70%的老人）
                320000.0     // 本月发放
            ));
            
            // 老人类型统计 - 使用真实的字典数据
            List<Map<String, Object>> elderlyTypeData = dashboardStatsDao.getElderlyTypeStatistics();
            DashboardStatsDTO.ElderlyTypeStatsDTO elderlyTypeStats = new DashboardStatsDTO.ElderlyTypeStatsDTO();
            
            if (elderlyTypeData != null && !elderlyTypeData.isEmpty()) {
                for (Map<String, Object> data : elderlyTypeData) {
                    String elderlyType = (String) data.get("elderlyType");
                    Long count = ((Number) data.get("count")).longValue();
                    
                    switch (elderlyType) {
                        case "normal":
                            elderlyTypeStats.setNormalCount(count);
                            break;
                        case "empty_nest":
                            elderlyTypeStats.setEmptyNestCount(count);
                            break;
                        case "living_alone":
                            elderlyTypeStats.setLivingAloneCount(count);
                            break;
                        case "disabled":
                            elderlyTypeStats.setDisabledCount(count);
                            break;
                        case "elderly":
                            elderlyTypeStats.setElderlyCount(count);
                            break;
                        case "low_income":
                            elderlyTypeStats.setLowIncomeCount(count);
                            break;
                        case "special_care":
                            elderlyTypeStats.setSpecialCareCount(count);
                            break;
                    }
                }
            }
            
            stats.setElderlyTypeStats(elderlyTypeStats);
            
            // 能力评估统计
            Long fullAbilityCount = dashboardStatsDao.countElderlyByAbilityAssessment("能力完好");
            Long mildDisabilityCount = dashboardStatsDao.countElderlyByAbilityAssessment("轻度失能");
            Long moderateDisabilityCount = dashboardStatsDao.countElderlyByAbilityAssessment("中度失能");
            Long severeDisabilityCount = dashboardStatsDao.countElderlyByAbilityAssessment("重度失能");
            Long notAssessedCount = totalElderly != null ? totalElderly - 
                (fullAbilityCount != null ? fullAbilityCount : 0L) -
                (mildDisabilityCount != null ? mildDisabilityCount : 0L) -
                (moderateDisabilityCount != null ? moderateDisabilityCount : 0L) -
                (severeDisabilityCount != null ? severeDisabilityCount : 0L) : 0L;
            
            stats.setAbilityStats(new DashboardStatsDTO.AbilityAssessmentStatsDTO(
                fullAbilityCount != null ? fullAbilityCount : 0L,
                mildDisabilityCount != null ? mildDisabilityCount : 0L,
                moderateDisabilityCount != null ? moderateDisabilityCount : 0L,
                severeDisabilityCount != null ? severeDisabilityCount : 0L,
                notAssessedCount > 0 ? notAssessedCount : 0L
            ));
            
            // 年龄分布统计
            Long age60to69Count = dashboardStatsDao.countElderlyByAgeRange(60, 69);
            Long age70to79Count = dashboardStatsDao.countElderlyByAgeRange(70, 79);
            Long age80to89Count = dashboardStatsDao.countElderlyByAgeRange(80, 89);
            Long age90PlusCount = dashboardStatsDao.countElderlyByAgeRange(90, null);
            
            stats.setAgeDistribution(new DashboardStatsDTO.AgeDistributionStatsDTO(
                age60to69Count != null ? age60to69Count : 0L,
                age70to79Count != null ? age70to79Count : 0L,
                age80to89Count != null ? age80to89Count : 0L,
                age90PlusCount != null ? age90PlusCount : 0L
            ));
            
            // 设备统计
            Long sosDeviceCount = dashboardStatsDao.countDevicesByType("SOS");
            Long smokeDetectorCount = dashboardStatsDao.countDevicesByType("烟感");
            Long waterLeakCount = dashboardStatsDao.countDevicesByType("水浸");
            Long fallDetectorCount = dashboardStatsDao.countDevicesByType("跌倒");
            Long gasLeakCount = dashboardStatsDao.countDevicesByType("燃气");
            Long onlineCount = dashboardStatsDao.countDevicesByStatus("在线");
            Long offlineCount = dashboardStatsDao.countDevicesByStatus("离线");
            Long faultCount = dashboardStatsDao.countDevicesByStatus("故障");
            
            // 获取设备运行状态详细信息
            List<Map<String, Object>> deviceStatusData = dashboardStatsDao.getDeviceStatusDetails();
            List<DashboardStatsDTO.DeviceStatusDetailDTO> deviceStatusDetails = new ArrayList<>();
            
            if (deviceStatusData != null) {
                for (Map<String, Object> data : deviceStatusData) {
                    String deviceName = (String) data.get("deviceName");
                    Long totalCount = ((Number) data.get("totalCount")).longValue();
                    Long deviceFaultCount = ((Number) data.get("faultCount")).longValue();
                    Long deviceOnlineCount = ((Number) data.get("onlineCount")).longValue();
                    
                    deviceStatusDetails.add(new DashboardStatsDTO.DeviceStatusDetailDTO(
                        deviceName, totalCount, deviceFaultCount, deviceOnlineCount
                    ));
                }
            }
            
            DashboardStatsDTO.DeviceStatsDTO deviceStats = new DashboardStatsDTO.DeviceStatsDTO();
            deviceStats.setSosDeviceCount(sosDeviceCount != null ? sosDeviceCount : 0L);
            deviceStats.setSmokeDetectorCount(smokeDetectorCount != null ? smokeDetectorCount : 0L);
            deviceStats.setWaterLeakCount(waterLeakCount != null ? waterLeakCount : 0L);
            deviceStats.setFallDetectorCount(fallDetectorCount != null ? fallDetectorCount : 0L);
            deviceStats.setGasLeakCount(gasLeakCount != null ? gasLeakCount : 0L);
            deviceStats.setOnlineCount(onlineCount != null ? onlineCount : 0L);
            deviceStats.setOfflineCount(offlineCount != null ? offlineCount : 0L);
            deviceStats.setFaultCount(faultCount != null ? faultCount : 0L);
            deviceStats.setDeviceStatusDetails(deviceStatusDetails);
            stats.setDeviceStats(deviceStats);
            
            // 告警统计
            Long todayAlarmCount = dashboardStatsDao.countTodayAlarms();
            Long weekAlarmCount = dashboardStatsDao.countWeekAlarms();
            Long monthAlarmCount = dashboardStatsDao.countMonthAlarms();
            Long unhandledCount = dashboardStatsDao.countUnhandledAlarms();
            
            stats.setAlarmStats(new DashboardStatsDTO.AlarmStatsDTO(
                todayAlarmCount != null ? todayAlarmCount : 0L,
                weekAlarmCount != null ? weekAlarmCount : 0L,
                monthAlarmCount != null ? monthAlarmCount : 0L,
                unhandledCount != null ? unhandledCount : 0L
            ));
            
            // 地图数据
            DashboardStatsDTO.MapDataDTO mapData = new DashboardStatsDTO.MapDataDTO();
            
            // 社区数据
            List<Map<String, Object>> communityData = dashboardStatsDao.getCommunityStats();
            List<DashboardStatsDTO.MapDataDTO.CommunityDataDTO> communities = new ArrayList<>();
            
            System.out.println("=== 社区数据调试 ===");
            System.out.println("communityData: " + communityData);
            System.out.println("communityData size: " + (communityData != null ? communityData.size() : "null"));
            
            if (communityData != null && !communityData.isEmpty()) {
                System.out.println("开始处理社区数据...");
                for (int i = 0; i < communityData.size(); i++) {
                    Map<String, Object> data = communityData.get(i);
                    System.out.println("处理第" + (i+1) + "个社区: " + data);
                    
                    try {
                        String name = (String) data.get("name");
                        Double longitude = ((Number) data.get("longitude")).doubleValue();
                        Double latitude = ((Number) data.get("latitude")).doubleValue();
                        Long elderlyCount = ((Number) data.get("elderlyCount")).longValue();
                        Long facilityCount = ((Number) data.get("facilityCount")).longValue();
                        String type = (String) data.get("type");
                        
                        System.out.println("社区数据解析成功: " + name + " (" + longitude + ", " + latitude + ")");
                        
                        communities.add(new DashboardStatsDTO.MapDataDTO.CommunityDataDTO(
                            name, longitude, latitude, elderlyCount, facilityCount, type
                        ));
                    } catch (Exception e) {
                        System.err.println("处理社区数据异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("社区数据处理完成，总数: " + communities.size());
            } else {
                System.out.println("社区数据为空或null");
            }
            
            // 机构数据
            List<Map<String, Object>> organizationData = dashboardStatsDao.getOrganizationStats();
            List<DashboardStatsDTO.MapDataDTO.OrganizationDataDTO> organizations = new ArrayList<>();
            
            if (organizationData != null) {
                for (Map<String, Object> data : organizationData) {
                    String name = (String) data.get("name");
                    Double longitude = ((Number) data.get("longitude")).doubleValue();
                    Double latitude = ((Number) data.get("latitude")).doubleValue();
                    String type = (String) data.get("type");
                    Long bedCount = data.get("bedCount") != null ? ((Number) data.get("bedCount")).longValue() : 0L;
                    Long staffCount = data.get("staffCount") != null ? ((Number) data.get("staffCount")).longValue() : 0L;
                    Long serviceCount = data.get("serviceCount") != null ? ((Number) data.get("serviceCount")).longValue() : 0L;
                    
                    organizations.add(new DashboardStatsDTO.MapDataDTO.OrganizationDataDTO(
                        name, longitude, latitude, type, bedCount, staffCount, serviceCount
                    ));
                }
            }
            
            // 移除老人位置数据展示
            // List<Map<String, Object>> elderlyLocationData = dashboardStatsDao.getElderlyLocationStats();
            List<DashboardStatsDTO.MapDataDTO.ElderlyDataDTO> elderly = new ArrayList<>();
            
            // 不再查询和返回老人位置数据
            // if (elderlyLocationData != null) {
            //     for (Map<String, Object> data : elderlyLocationData) {
            //         String elderlyName = (String) data.get("elderlyName");
            //         String community = (String) data.get("community");
            //         String address = (String) data.get("address");
            //         Double longitude = ((Number) data.get("longitude")).doubleValue();
            //         Double latitude = ((Number) data.get("latitude")).doubleValue();
            //         String elderlyType = (String) data.get("elderlyType");
            //         String abilityAssessment = (String) data.get("abilityAssessment");
            //         Integer age = data.get("age") != null ? ((Number) data.get("age")).intValue() : 0;
            //         String gender = (String) data.get("gender");
            //         
            //         elderly.add(new DashboardStatsDTO.MapDataDTO.ElderlyDataDTO(
            //             elderlyName, community, address, longitude, latitude, elderlyType, abilityAssessment, age, gender
            //         ));
            //     }
            // }
            
            // 告警位置数据
            List<Map<String, Object>> alarmLocationData = dashboardStatsDao.getAlarmLocationStats();
            List<DashboardStatsDTO.MapDataDTO.AlarmDataDTO> alarms = new ArrayList<>();
            
            if (alarmLocationData != null) {
                for (Map<String, Object> data : alarmLocationData) {
                    String alarmType = (String) data.get("alarmType");
                    String alarmLevel = (String) data.get("alarmLevel");
                    String location = (String) data.get("location");
                    String alarmTime = data.get("alarmTime") != null ? data.get("alarmTime").toString() : "";
                    String processStatus = (String) data.get("processStatus");
                    Double longitude = ((Number) data.get("longitude")).doubleValue();
                    Double latitude = ((Number) data.get("latitude")).doubleValue();
                    
                    alarms.add(new DashboardStatsDTO.MapDataDTO.AlarmDataDTO(
                        alarmType, alarmLevel, location, alarmTime, processStatus, longitude, latitude
                    ));
                }
            }
            
            mapData.setCommunities(communities);
            mapData.setOrganizations(organizations);
            mapData.setElderly(elderly);
            mapData.setAlarms(alarms);
            stats.setMapData(mapData);
            
        } catch (Exception e) {
            System.err.println("=== 数据库查询异常 ===");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常消息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== 异常详情结束 ===");
            // 如果数据库查询失败，返回默认值
            return getDefaultStats();
        }
        
        return stats;
    }
    
    /**
     * 获取默认统计数据（当数据库查询失败时使用）
     */
    private DashboardStatsDTO getDefaultStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        stats.setElderlyStats(new DashboardStatsDTO.ElderlyStatsDTO(0L, 0L, 0L, 0L, 0L));
        stats.setFacilityStats(new DashboardStatsDTO.FacilityStatsDTO(0L, 0L, 0L, 0L));
        stats.setStaffStats(new DashboardStatsDTO.StaffStatsDTO(0L, 0L, 0L, 0L));
        stats.setSubsidyStats(new DashboardStatsDTO.SubsidyStatsDTO(0.0, 0L, 0.0));
        stats.setElderlyTypeStats(new DashboardStatsDTO.ElderlyTypeStatsDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L));
        stats.setAbilityStats(new DashboardStatsDTO.AbilityAssessmentStatsDTO(0L, 0L, 0L, 0L, 0L));
        stats.setAgeDistribution(new DashboardStatsDTO.AgeDistributionStatsDTO(0L, 0L, 0L, 0L));
        DashboardStatsDTO.DeviceStatsDTO defaultDeviceStats = new DashboardStatsDTO.DeviceStatsDTO();
        defaultDeviceStats.setSosDeviceCount(0L);
        defaultDeviceStats.setSmokeDetectorCount(0L);
        defaultDeviceStats.setWaterLeakCount(0L);
        defaultDeviceStats.setFallDetectorCount(0L);
        defaultDeviceStats.setGasLeakCount(0L);
        defaultDeviceStats.setOnlineCount(0L);
        defaultDeviceStats.setOfflineCount(0L);
        defaultDeviceStats.setFaultCount(0L);
        defaultDeviceStats.setDeviceStatusDetails(new ArrayList<>());
        stats.setDeviceStats(defaultDeviceStats);
        stats.setAlarmStats(new DashboardStatsDTO.AlarmStatsDTO(0L, 0L, 0L, 0L));
        
        DashboardStatsDTO.MapDataDTO mapData = new DashboardStatsDTO.MapDataDTO();
        mapData.setCommunities(new ArrayList<>());
        stats.setMapData(mapData);
        
        return stats;
    }
    
    /**
     * 获取实时告警数据
     */
    public List<AlarmDataDTO> getRecentAlarms() {
        try {
            List<Map<String, Object>> alarmData = dashboardStatsDao.getRecentAlarms(10);
            List<AlarmDataDTO> alarms = new ArrayList<>();
            
            if (alarmData != null) {
                for (Map<String, Object> data : alarmData) {
                    String type = (String) data.get("type");
                    String location = (String) data.get("location");
                    String time = (String) data.get("time");
                    String level = (String) data.get("level");
                    String status = (String) data.get("status");
                    
                    alarms.add(new AlarmDataDTO(type, location, time, level, status));
                }
            }
            
            // 如果没有告警数据，返回空列表
            return alarms;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 如果数据库查询失败，返回空列表
            return new ArrayList<>();
        }
    }
    
    /**
     * 告警数据DTO
     */
    public static class AlarmDataDTO {
        private String type;        // 告警类型
        private String location;    // 位置信息
        private String time;        // 告警时间
        private String level;       // 告警级别
        private String status;      // 处理状态
        
        public AlarmDataDTO(String type, String location, String time, String level, String status) {
            this.type = type;
            this.location = location;
            this.time = time;
            this.level = level;
            this.status = status;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
} 