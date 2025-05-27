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
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacilityStatsDTO {
        private Long totalCount;        // 总机构数
        private Long homeCareCount;     // 居家养老
        private Long dayCareCount;      // 日照中心
        private Long institutionCount;  // 养老机构
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaffStatsDTO {
        private Long totalCount;        // 总人数
        private Long nurseCount;        // 护理员
        private Long doctorCount;       // 医生
        private Long socialWorkerCount; // 社工
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubsidyStatsDTO {
        private Double totalAmount;     // 总金额
        private Long beneficiaryCount;  // 受益人数
        private Double monthlyAmount;   // 本月发放
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
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AgeDistributionStatsDTO {
        private Long age60to69Count;    // 60-69岁
        private Long age70to79Count;    // 70-79岁
        private Long age80to89Count;    // 80-89岁
        private Long age90PlusCount;    // 90岁以上
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
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceStatusDetailDTO {
        private String deviceName;          // 设备名称
        private Long totalCount;            // 设备总数
        private Long faultCount;            // 故障数量
        private Long onlineCount;           // 在线数量
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlarmStatsDTO {
        private Long todayAlarmCount;       // 今日告警
        private Long weekAlarmCount;        // 本周告警
        private Long monthAlarmCount;       // 本月告警
        private Long unhandledCount;        // 未处理告警
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MapDataDTO {
        private java.util.List<CommunityDataDTO> communities;
        private java.util.List<OrganizationDataDTO> organizations;
        private java.util.List<ElderlyDataDTO> elderly;
        private java.util.List<AlarmDataDTO> alarms;
        
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
        }
    }
} 