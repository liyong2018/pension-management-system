package com.example.pension.service;

import com.example.pension.dao.DashboardStatsDao;
import com.example.pension.dao.SmartDeviceDao;
import com.example.pension.dto.DashboardStatsDTO;
import com.example.pension.dto.DeviceTypeDetailedStatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页数据服务
 */
@Service
public class DashboardService {
    
    @Autowired
    private DashboardStatsDao dashboardStatsDao;
    
    @Autowired
    private SmartDeviceDao smartDeviceDao;
    
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
            
            DashboardStatsDTO.AbilityAssessmentStatsDTO abilityStats = new DashboardStatsDTO.AbilityAssessmentStatsDTO();
            abilityStats.setFullAbilityCount(fullAbilityCount != null ? fullAbilityCount : 0L);
            abilityStats.setMildDisabilityCount(mildDisabilityCount != null ? mildDisabilityCount : 0L);
            abilityStats.setModerateDisabilityCount(moderateDisabilityCount != null ? moderateDisabilityCount : 0L);
            abilityStats.setSevereDisabilityCount(severeDisabilityCount != null ? severeDisabilityCount : 0L);
            abilityStats.setNotAssessedCount(notAssessedCount > 0 ? notAssessedCount : 0L);
            stats.setAbilityStats(abilityStats);
            
            // 年龄分布统计
            Long age60to69Count = dashboardStatsDao.countElderlyByAgeRange(60, 69);
            Long age70to79Count = dashboardStatsDao.countElderlyByAgeRange(70, 79);
            Long age80to89Count = dashboardStatsDao.countElderlyByAgeRange(80, 89);
            Long age90PlusCount = dashboardStatsDao.countElderlyByAgeRange(90, null);
            
            DashboardStatsDTO.AgeDistributionStatsDTO ageDistribution = new DashboardStatsDTO.AgeDistributionStatsDTO();
            ageDistribution.setAge60to69Count(age60to69Count != null ? age60to69Count : 0L);
            ageDistribution.setAge70to79Count(age70to79Count != null ? age70to79Count : 0L);
            ageDistribution.setAge80to89Count(age80to89Count != null ? age80to89Count : 0L);
            ageDistribution.setAge90PlusCount(age90PlusCount != null ? age90PlusCount : 0L);
            stats.setAgeDistribution(ageDistribution);
            
            // 设备统计
            Long sosDeviceCount = dashboardStatsDao.countDevicesByType("SOS");
            Long smokeDetectorCount = dashboardStatsDao.countDevicesByType("烟感");
            Long waterLeakCount = dashboardStatsDao.countDevicesByType("水浸");
            Long fallDetectorCount = dashboardStatsDao.countDevicesByType("跌倒");
            Long gasLeakCount = dashboardStatsDao.countDevicesByType("燃气");
            Long onlineCount = dashboardStatsDao.countDevicesByStatus("在线");
            Long offlineCount = dashboardStatsDao.countDevicesByStatus("离线");
            Long faultCount = dashboardStatsDao.countDevicesByStatus("故障");
            
            // 获取设备运行状态详细信息 - 改为从SmartDeviceDao获取
            List<DeviceTypeDetailedStatDTO> deviceStatusData = smartDeviceDao.getDeviceStatisticsGroupedByType();
            List<DashboardStatsDTO.DeviceStatusDetailDTO> deviceStatusDetails = new ArrayList<>();
            
            if (deviceStatusData != null) {
                deviceStatusDetails = deviceStatusData.stream()
                        .map(stat -> {
                            DashboardStatsDTO.DeviceStatusDetailDTO detail = new DashboardStatsDTO.DeviceStatusDetailDTO();
                            detail.setDeviceName(stat.getDeviceType());
                            detail.setTotalCount(stat.getTotalCount());
                            detail.setFaultCount(0L); // faultCount is not available in DeviceTypeDetailedStatDTO, default to 0
                            detail.setOnlineCount(stat.getOnlineCount());
                            return detail;
                        })
                        .collect(Collectors.toList());
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
            
            DashboardStatsDTO.AlarmStatsDTO alarmStats = new DashboardStatsDTO.AlarmStatsDTO();
            alarmStats.setTodayAlarmCount(todayAlarmCount != null ? todayAlarmCount : 0L);
            alarmStats.setWeekAlarmCount(weekAlarmCount != null ? weekAlarmCount : 0L);
            alarmStats.setMonthAlarmCount(monthAlarmCount != null ? monthAlarmCount : 0L);
            alarmStats.setUnhandledCount(unhandledCount != null ? unhandledCount : 0L);
            stats.setAlarmStats(alarmStats);
            
            // 地图数据
            DashboardStatsDTO.MapDataDTO mapData = new DashboardStatsDTO.MapDataDTO();
            
            // 社区数据
            List<Map<String, Object>> communityData = dashboardStatsDao.getCommunityStats();
            List<DashboardStatsDTO.MapDataDTO.CommunityDataDTO> communities = new ArrayList<>();
            
            if (communityData != null && !communityData.isEmpty()) {
                for (Map<String, Object> data : communityData) {
                    try {
                        String name = (String) data.get("name");
                        Double longitude = ((Number) data.get("longitude")).doubleValue();
                        Double latitude = ((Number) data.get("latitude")).doubleValue();
                        Long elderlyCount = ((Number) data.get("elderlyCount")).longValue();
                        Long facilityCount = ((Number) data.get("facilityCount")).longValue();
                        String type = (String) data.get("type");
                        
                        DashboardStatsDTO.MapDataDTO.CommunityDataDTO community = new DashboardStatsDTO.MapDataDTO.CommunityDataDTO();
                        community.setName(name);
                        community.setLongitude(longitude);
                        community.setLatitude(latitude);
                        community.setElderlyCount(elderlyCount);
                        community.setFacilityCount(facilityCount);
                        community.setType(type);
                        communities.add(community);
                    } catch (Exception e) {
                        // 记录错误但继续处理其他数据
                        e.printStackTrace();
                    }
                }
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
                    
                    DashboardStatsDTO.MapDataDTO.OrganizationDataDTO org = new DashboardStatsDTO.MapDataDTO.OrganizationDataDTO();
                    org.setName(name);
                    org.setLongitude(longitude);
                    org.setLatitude(latitude);
                    org.setType(type);
                    org.setBedCount(bedCount);
                    org.setStaffCount(staffCount);
                    org.setServiceCount(serviceCount);
                    organizations.add(org);
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
                    
                    DashboardStatsDTO.MapDataDTO.AlarmDataDTO alarm = new DashboardStatsDTO.MapDataDTO.AlarmDataDTO();
                    alarm.setAlarmType(alarmType);
                    alarm.setAlarmLevel(alarmLevel);
                    alarm.setLocation(location);
                    alarm.setAlarmTime(alarmTime);
                    alarm.setProcessStatus(processStatus);
                    alarm.setLongitude(longitude);
                    alarm.setLatitude(latitude);
                    alarms.add(alarm);
                }
            }
            
            mapData.setCommunities(communities);
            mapData.setOrganizations(organizations);
            mapData.setElderly(elderly);
            mapData.setAlarms(alarms);
            stats.setMapData(mapData);
            
        } catch (Exception e) {
            // 如果数据库查询失败，返回默认值
            e.printStackTrace();
            return getDefaultStats();
        }
        
        return stats;
    }
    
    /**
     * 获取默认统计数据（当数据库查询失败时使用）
     */
    private DashboardStatsDTO getDefaultStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        DashboardStatsDTO.ElderlyStatsDTO elderlyStats = new DashboardStatsDTO.ElderlyStatsDTO();
        elderlyStats.setTotalCount(0L);
        elderlyStats.setOver80Count(0L);
        elderlyStats.setLivingAloneCount(0L);
        elderlyStats.setDisabledCount(0L);
        elderlyStats.setLowIncomeCount(0L);
        stats.setElderlyStats(elderlyStats);
        
        DashboardStatsDTO.FacilityStatsDTO facilityStats = new DashboardStatsDTO.FacilityStatsDTO();
        facilityStats.setTotalCount(0L);
        facilityStats.setHomeCareCount(0L);
        facilityStats.setDayCareCount(0L);
        facilityStats.setInstitutionCount(0L);
        stats.setFacilityStats(facilityStats);
        
        DashboardStatsDTO.StaffStatsDTO staffStats = new DashboardStatsDTO.StaffStatsDTO();
        staffStats.setTotalCount(0L);
        staffStats.setNurseCount(0L);
        staffStats.setDoctorCount(0L);
        staffStats.setSocialWorkerCount(0L);
        stats.setStaffStats(staffStats);
        
        DashboardStatsDTO.SubsidyStatsDTO subsidyStats = new DashboardStatsDTO.SubsidyStatsDTO();
        subsidyStats.setTotalAmount(0.0);
        subsidyStats.setBeneficiaryCount(0L);
        subsidyStats.setMonthlyAmount(0.0);
        stats.setSubsidyStats(subsidyStats);
        
        DashboardStatsDTO.ElderlyTypeStatsDTO elderlyTypeStats = new DashboardStatsDTO.ElderlyTypeStatsDTO();
        elderlyTypeStats.setNormalCount(0L);
        elderlyTypeStats.setEmptyNestCount(0L);
        elderlyTypeStats.setLivingAloneCount(0L);
        elderlyTypeStats.setDisabledCount(0L);
        elderlyTypeStats.setElderlyCount(0L);
        elderlyTypeStats.setLowIncomeCount(0L);
        elderlyTypeStats.setSpecialCareCount(0L);
        stats.setElderlyTypeStats(elderlyTypeStats);
        
        DashboardStatsDTO.AbilityAssessmentStatsDTO abilityStats = new DashboardStatsDTO.AbilityAssessmentStatsDTO();
        abilityStats.setFullAbilityCount(0L);
        abilityStats.setMildDisabilityCount(0L);
        abilityStats.setModerateDisabilityCount(0L);
        abilityStats.setSevereDisabilityCount(0L);
        abilityStats.setNotAssessedCount(0L);
        stats.setAbilityStats(abilityStats);
        
        DashboardStatsDTO.AgeDistributionStatsDTO ageDistribution = new DashboardStatsDTO.AgeDistributionStatsDTO();
        ageDistribution.setAge60to69Count(0L);
        ageDistribution.setAge70to79Count(0L);
        ageDistribution.setAge80to89Count(0L);
        ageDistribution.setAge90PlusCount(0L);
        stats.setAgeDistribution(ageDistribution);
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
        DashboardStatsDTO.AlarmStatsDTO defaultAlarmStats = new DashboardStatsDTO.AlarmStatsDTO();
        defaultAlarmStats.setTotalCount(0L);
        defaultAlarmStats.setHighCount(0L);
        defaultAlarmStats.setMediumCount(0L);
        defaultAlarmStats.setLowCount(0L);
        stats.setAlarmStats(defaultAlarmStats);
        
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