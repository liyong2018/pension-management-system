package com.example.pension.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 首页统计数据DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    
    public ElderlyStatsDTO getElderlyStats() {
        return elderlyStats;
    }
    
    public void setElderlyStats(ElderlyStatsDTO elderlyStats) {
        this.elderlyStats = elderlyStats;
    }
    
    public FacilityStatsDTO getFacilityStats() {
        return facilityStats;
    }
    
    public void setFacilityStats(FacilityStatsDTO facilityStats) {
        this.facilityStats = facilityStats;
    }
    
    public StaffStatsDTO getStaffStats() {
        return staffStats;
    }
    
    public void setStaffStats(StaffStatsDTO staffStats) {
        this.staffStats = staffStats;
    }
    
    public SubsidyStatsDTO getSubsidyStats() {
        return subsidyStats;
    }
    
    public void setSubsidyStats(SubsidyStatsDTO subsidyStats) {
        this.subsidyStats = subsidyStats;
    }
    
    public ElderlyTypeStatsDTO getElderlyTypeStats() {
        return elderlyTypeStats;
    }
    
    public void setElderlyTypeStats(ElderlyTypeStatsDTO elderlyTypeStats) {
        this.elderlyTypeStats = elderlyTypeStats;
    }
    
    public AbilityAssessmentStatsDTO getAbilityStats() {
        return abilityStats;
    }
    
    public void setAbilityStats(AbilityAssessmentStatsDTO abilityStats) {
        this.abilityStats = abilityStats;
    }
    
    public AgeDistributionStatsDTO getAgeDistribution() {
        return ageDistribution;
    }
    
    public void setAgeDistribution(AgeDistributionStatsDTO ageDistribution) {
        this.ageDistribution = ageDistribution;
    }
    
    public DeviceStatsDTO getDeviceStats() {
        return deviceStats;
    }
    
    public void setDeviceStats(DeviceStatsDTO deviceStats) {
        this.deviceStats = deviceStats;
    }
    
    public AlarmStatsDTO getAlarmStats() {
        return alarmStats;
    }
    
    public void setAlarmStats(AlarmStatsDTO alarmStats) {
        this.alarmStats = alarmStats;
    }
    
    public MapDataDTO getMapData() {
        return mapData;
    }
    
    public void setMapData(MapDataDTO mapData) {
        this.mapData = mapData;
    }
    
    /**
     * 老龄人口统计
     */
    private ElderlyStatsDTO elderlyStats;
    
    /**
     * 养老机构及设施统计
     */
    private FacilityStatsDTO facilityStats;
    
    /**
     * 从业人员统计
     */
    private StaffStatsDTO staffStats;
    
    /**
     * 发放补贴统计
     */
    private SubsidyStatsDTO subsidyStats;
    
    /**
     * 老人类型统计
     */
    private ElderlyTypeStatsDTO elderlyTypeStats;
    
    /**
     * 能力评估统计
     */
    private AbilityAssessmentStatsDTO abilityStats;
    
    /**
     * 年龄分布统计
     */
    private AgeDistributionStatsDTO ageDistribution;
    
    /**
     * 设备统计
     */
    private DeviceStatsDTO deviceStats;
    
    /**
     * 告警统计
     */
    private AlarmStatsDTO alarmStats;
    
    /**
     * 地图数据
     */
    private MapDataDTO mapData;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElderlyStatsDTO {
        private Long totalCount;        // 总人数
        private Long over80Count;       // 80岁以上
        private Long livingAloneCount;  // 独居老人
        private Long disabledCount;     // 失能老人
        private Long lowIncomeCount;    // 低收入老人
        
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getOver80Count() {
            return over80Count;
        }
        
        public void setOver80Count(Long over80Count) {
            this.over80Count = over80Count;
        }
        
        public Long getLivingAloneCount() {
            return livingAloneCount;
        }
        
        public void setLivingAloneCount(Long livingAloneCount) {
            this.livingAloneCount = livingAloneCount;
        }
        
        public Long getDisabledCount() {
            return disabledCount;
        }
        
        public void setDisabledCount(Long disabledCount) {
            this.disabledCount = disabledCount;
        }
        
        public Long getLowIncomeCount() {
            return lowIncomeCount;
        }
        
        public void setLowIncomeCount(Long lowIncomeCount) {
            this.lowIncomeCount = lowIncomeCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacilityStatsDTO {
        private Long totalCount;        // 总机构数
        private Long homeCareCount;     // 居家养老
        private Long dayCareCount;      // 日照中心
        private Long institutionCount;  // 养老机构
        
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getHomeCareCount() {
            return homeCareCount;
        }
        
        public void setHomeCareCount(Long homeCareCount) {
            this.homeCareCount = homeCareCount;
        }
        
        public Long getDayCareCount() {
            return dayCareCount;
        }
        
        public void setDayCareCount(Long dayCareCount) {
            this.dayCareCount = dayCareCount;
        }
        
        public Long getInstitutionCount() {
            return institutionCount;
        }
        
        public void setInstitutionCount(Long institutionCount) {
            this.institutionCount = institutionCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaffStatsDTO {
        private Long totalCount;        // 总人数
        private Long nurseCount;        // 护理员
        private Long doctorCount;       // 医生
        private Long socialWorkerCount; // 社工
        
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getNurseCount() {
            return nurseCount;
        }
        
        public void setNurseCount(Long nurseCount) {
            this.nurseCount = nurseCount;
        }
        
        public Long getDoctorCount() {
            return doctorCount;
        }
        
        public void setDoctorCount(Long doctorCount) {
            this.doctorCount = doctorCount;
        }
        
        public Long getSocialWorkerCount() {
            return socialWorkerCount;
        }
        
        public void setSocialWorkerCount(Long socialWorkerCount) {
            this.socialWorkerCount = socialWorkerCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubsidyStatsDTO {
        private Double totalAmount;     // 总金额
        private Long beneficiaryCount;  // 受益人数
        private Double monthlyAmount;   // 本月发放
        
        public Double getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }
        
        public Long getBeneficiaryCount() {
            return beneficiaryCount;
        }
        
        public void setBeneficiaryCount(Long beneficiaryCount) {
            this.beneficiaryCount = beneficiaryCount;
        }
        
        public Double getMonthlyAmount() {
            return monthlyAmount;
        }
        
        public void setMonthlyAmount(Double monthlyAmount) {
            this.monthlyAmount = monthlyAmount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElderlyTypeStatsDTO {
        private Long normalCount;       // 正常老人
        private Long emptyNestCount;    // 空巢老人
        private Long livingAloneCount;  // 独居老人
        private Long disabledCount;     // 残疾老人
        private Long elderlyCount;      // 高龄老人
        private Long lowIncomeCount;    // 低收入老人
        private Long specialCareCount;  // 特殊照护老人
        
        public Long getNormalCount() {
            return normalCount;
        }
        
        public void setNormalCount(Long normalCount) {
            this.normalCount = normalCount;
        }
        
        public Long getEmptyNestCount() {
            return emptyNestCount;
        }
        
        public void setEmptyNestCount(Long emptyNestCount) {
            this.emptyNestCount = emptyNestCount;
        }
        
        public Long getLivingAloneCount() {
            return livingAloneCount;
        }
        
        public void setLivingAloneCount(Long livingAloneCount) {
            this.livingAloneCount = livingAloneCount;
        }
        
        public Long getDisabledCount() {
            return disabledCount;
        }
        
        public void setDisabledCount(Long disabledCount) {
            this.disabledCount = disabledCount;
        }
        
        public Long getElderlyCount() {
            return elderlyCount;
        }
        
        public void setElderlyCount(Long elderlyCount) {
            this.elderlyCount = elderlyCount;
        }
        
        public Long getLowIncomeCount() {
            return lowIncomeCount;
        }
        
        public void setLowIncomeCount(Long lowIncomeCount) {
            this.lowIncomeCount = lowIncomeCount;
        }
        
        public Long getSpecialCareCount() {
            return specialCareCount;
        }
        
        public void setSpecialCareCount(Long specialCareCount) {
            this.specialCareCount = specialCareCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AbilityAssessmentStatsDTO {
        private Long fullAbilityCount;      // 能力完好
        private Long mildDisabilityCount;   // 轻度失能
        private Long moderateDisabilityCount; // 中度失能
        private Long severeDisabilityCount;   // 重度失能
        private Long notAssessedCount;        // 未评估
        
        public Long getFullAbilityCount() {
            return fullAbilityCount;
        }
        
        public void setFullAbilityCount(Long fullAbilityCount) {
            this.fullAbilityCount = fullAbilityCount;
        }
        
        public Long getMildDisabilityCount() {
            return mildDisabilityCount;
        }
        
        public void setMildDisabilityCount(Long mildDisabilityCount) {
            this.mildDisabilityCount = mildDisabilityCount;
        }
        
        public Long getModerateDisabilityCount() {
            return moderateDisabilityCount;
        }
        
        public void setModerateDisabilityCount(Long moderateDisabilityCount) {
            this.moderateDisabilityCount = moderateDisabilityCount;
        }
        
        public Long getSevereDisabilityCount() {
            return severeDisabilityCount;
        }
        
        public void setSevereDisabilityCount(Long severeDisabilityCount) {
            this.severeDisabilityCount = severeDisabilityCount;
        }
        
        public Long getNotAssessedCount() {
            return notAssessedCount;
        }
        
        public void setNotAssessedCount(Long notAssessedCount) {
            this.notAssessedCount = notAssessedCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AgeDistributionStatsDTO {
        private Long age60to69Count;    // 60-69岁
        private Long age70to79Count;    // 70-79岁
        private Long age80to89Count;    // 80-89岁
        private Long age90PlusCount;    // 90岁以上
        
        public Long getAge60to69Count() {
            return age60to69Count;
        }
        
        public void setAge60to69Count(Long age60to69Count) {
            this.age60to69Count = age60to69Count;
        }
        
        public Long getAge70to79Count() {
            return age70to79Count;
        }
        
        public void setAge70to79Count(Long age70to79Count) {
            this.age70to79Count = age70to79Count;
        }
        
        public Long getAge80to89Count() {
            return age80to89Count;
        }
        
        public void setAge80to89Count(Long age80to89Count) {
            this.age80to89Count = age80to89Count;
        }
        
        public Long getAge90PlusCount() {
            return age90PlusCount;
        }
        
        public void setAge90PlusCount(Long age90PlusCount) {
            this.age90PlusCount = age90PlusCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceStatsDTO {
        private Long sosDeviceCount;        // SOS报警设备
        private Long smokeDetectorCount;    // 烟感设备
        private Long waterLeakCount;        // 水浸设备
        private Long fallDetectorCount;     // 跌倒设备
        private Long gasLeakCount;          // 燃气泄露设备
        private Long onlineCount;           // 在线设备
        private Long offlineCount;          // 离线设备
        private Long faultCount;            // 故障设备
        
        // 设备运行状态详细信息
        private java.util.List<DeviceStatusDetailDTO> deviceStatusDetails;
        
        public Long getSosDeviceCount() {
            return sosDeviceCount;
        }
        
        public void setSosDeviceCount(Long sosDeviceCount) {
            this.sosDeviceCount = sosDeviceCount;
        }
        
        public Long getSmokeDetectorCount() {
            return smokeDetectorCount;
        }
        
        public void setSmokeDetectorCount(Long smokeDetectorCount) {
            this.smokeDetectorCount = smokeDetectorCount;
        }
        
        public Long getWaterLeakCount() {
            return waterLeakCount;
        }
        
        public void setWaterLeakCount(Long waterLeakCount) {
            this.waterLeakCount = waterLeakCount;
        }
        
        public Long getFallDetectorCount() {
            return fallDetectorCount;
        }
        
        public void setFallDetectorCount(Long fallDetectorCount) {
            this.fallDetectorCount = fallDetectorCount;
        }
        
        public Long getGasLeakCount() {
            return gasLeakCount;
        }
        
        public void setGasLeakCount(Long gasLeakCount) {
            this.gasLeakCount = gasLeakCount;
        }
        
        public Long getOnlineCount() {
            return onlineCount;
        }
        
        public void setOnlineCount(Long onlineCount) {
            this.onlineCount = onlineCount;
        }
        
        public Long getOfflineCount() {
            return offlineCount;
        }
        
        public void setOfflineCount(Long offlineCount) {
            this.offlineCount = offlineCount;
        }
        
        public Long getFaultCount() {
            return faultCount;
        }
        
        public void setFaultCount(Long faultCount) {
            this.faultCount = faultCount;
        }
        
        public java.util.List<DeviceStatusDetailDTO> getDeviceStatusDetails() {
            return deviceStatusDetails;
        }
        
        public void setDeviceStatusDetails(java.util.List<DeviceStatusDetailDTO> deviceStatusDetails) {
            this.deviceStatusDetails = deviceStatusDetails;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceStatusDetailDTO {
        private String deviceName;          // 设备名称
        private Long totalCount;            // 设备总数
        private Long faultCount;            // 故障数量
        private Long onlineCount;           // 在线数量
        
        public String getDeviceName() {
            return deviceName;
        }
        
        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }
        
        public Long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }
        
        public Long getFaultCount() {
            return faultCount;
        }
        
        public void setFaultCount(Long faultCount) {
            this.faultCount = faultCount;
        }
        
        public Long getOnlineCount() {
            return onlineCount;
        }
        
        public void setOnlineCount(Long onlineCount) {
            this.onlineCount = onlineCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmStatsDTO {
        private Long todayAlarmCount;       // 今日告警
        private Long weekAlarmCount;        // 本周告警
        private Long monthAlarmCount;       // 本月告警
        private Long unhandledCount;        // 未处理告警
        
        public Long getTotalCount() {
            return todayAlarmCount;
        }
        
        public void setTotalCount(Long totalCount) {
            this.todayAlarmCount = totalCount;
        }
        
        public Long getHighCount() {
            return weekAlarmCount;
        }
        
        public void setHighCount(Long highCount) {
            this.weekAlarmCount = highCount;
        }
        
        public Long getMediumCount() {
            return monthAlarmCount;
        }
        
        public void setMediumCount(Long mediumCount) {
            this.monthAlarmCount = mediumCount;
        }
        
        public Long getLowCount() {
            return unhandledCount;
        }
        
        public void setLowCount(Long lowCount) {
            this.unhandledCount = lowCount;
        }
        
        public Long getTodayAlarmCount() {
            return todayAlarmCount;
        }
        
        public void setTodayAlarmCount(Long todayAlarmCount) {
            this.todayAlarmCount = todayAlarmCount;
        }
        
        public Long getWeekAlarmCount() {
            return weekAlarmCount;
        }
        
        public void setWeekAlarmCount(Long weekAlarmCount) {
            this.weekAlarmCount = weekAlarmCount;
        }
        
        public Long getMonthAlarmCount() {
            return monthAlarmCount;
        }
        
        public void setMonthAlarmCount(Long monthAlarmCount) {
            this.monthAlarmCount = monthAlarmCount;
        }
        
        public Long getUnhandledCount() {
            return unhandledCount;
        }
        
        public void setUnhandledCount(Long unhandledCount) {
            this.unhandledCount = unhandledCount;
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MapDataDTO {
        private java.util.List<CommunityDataDTO> communities;
        private java.util.List<OrganizationDataDTO> organizations;
        private java.util.List<ElderlyDataDTO> elderly;
        private java.util.List<AlarmDataDTO> alarms;
        
        public java.util.List<CommunityDataDTO> getCommunities() {
            return communities;
        }
        
        public void setCommunities(java.util.List<CommunityDataDTO> communities) {
            this.communities = communities;
        }
        
        public java.util.List<OrganizationDataDTO> getOrganizations() {
            return organizations;
        }
        
        public void setOrganizations(java.util.List<OrganizationDataDTO> organizations) {
            this.organizations = organizations;
        }
        
        public java.util.List<ElderlyDataDTO> getElderly() {
            return elderly;
        }
        
        public void setElderly(java.util.List<ElderlyDataDTO> elderly) {
            this.elderly = elderly;
        }
        
        public java.util.List<AlarmDataDTO> getAlarms() {
            return alarms;
        }
        
        public void setAlarms(java.util.List<AlarmDataDTO> alarms) {
            this.alarms = alarms;
        }
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class CommunityDataDTO {
            private String name;            // 社区名称
            private Double longitude;       // 经度
            private Double latitude;        // 纬度
            private Long elderlyCount;      // 老人数量
            private Long facilityCount;     // 机构数量
            private String type;            // 类型：community
            
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public Double getLongitude() {
                return longitude;
            }
            
            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
            
            public Double getLatitude() {
                return latitude;
            }
            
            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }
            
            public Long getElderlyCount() {
                return elderlyCount;
            }
            
            public void setElderlyCount(Long elderlyCount) {
                this.elderlyCount = elderlyCount;
            }
            
            public Long getFacilityCount() {
                return facilityCount;
            }
            
            public void setFacilityCount(Long facilityCount) {
                this.facilityCount = facilityCount;
            }
            
            public String getType() {
                return type;
            }
            
            public void setType(String type) {
                this.type = type;
            }
        }
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OrganizationDataDTO {
            private String name;            // 机构名称
            private Double longitude;       // 经度
            private Double latitude;        // 纬度
            private String type;            // 机构类型
            private Long bedCount;          // 床位数
            private Long staffCount;        // 员工数
            private Long serviceCount;      // 服务数
            
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public Double getLongitude() {
                return longitude;
            }
            
            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
            
            public Double getLatitude() {
                return latitude;
            }
            
            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }
            
            public String getType() {
                return type;
            }
            
            public void setType(String type) {
                this.type = type;
            }
            
            public Long getBedCount() {
                return bedCount;
            }
            
            public void setBedCount(Long bedCount) {
                this.bedCount = bedCount;
            }
            
            public Long getStaffCount() {
                return staffCount;
            }
            
            public void setStaffCount(Long staffCount) {
                this.staffCount = staffCount;
            }
            
            public Long getServiceCount() {
                return serviceCount;
            }
            
            public void setServiceCount(Long serviceCount) {
                this.serviceCount = serviceCount;
            }
        }
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ElderlyDataDTO {
            private String elderlyName;    // 老人姓名
            private String community;      // 所属社区
            private String address;        // 地址
            private Double longitude;      // 经度
            private Double latitude;       // 纬度
            private String elderlyType;    // 老人类型
            private String abilityAssessment; // 能力评估
            private Integer age;           // 年龄
            private String gender;         // 性别
        }
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AlarmDataDTO {
            private String alarmType;      // 告警类型
            private String alarmLevel;     // 告警级别
            private String location;       // 位置信息
            private String alarmTime;      // 告警时间
            private String processStatus;  // 处理状态
            private Double longitude;      // 经度
            private Double latitude;       // 纬度
            
            public String getAlarmType() {
                return alarmType;
            }
            
            public void setAlarmType(String alarmType) {
                this.alarmType = alarmType;
            }
            
            public String getAlarmLevel() {
                return alarmLevel;
            }
            
            public void setAlarmLevel(String alarmLevel) {
                this.alarmLevel = alarmLevel;
            }
            
            public String getLocation() {
                return location;
            }
            
            public void setLocation(String location) {
                this.location = location;
            }
            
            public String getAlarmTime() {
                return alarmTime;
            }
            
            public void setAlarmTime(String alarmTime) {
                this.alarmTime = alarmTime;
            }
            
            public String getProcessStatus() {
                return processStatus;
            }
            
            public void setProcessStatus(String processStatus) {
                this.processStatus = processStatus;
            }
            
            public Double getLongitude() {
                return longitude;
            }
            
            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
            
            public Double getLatitude() {
                return latitude;
            }
            
            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }
        }
    }
}