package com.example.pension.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceAlarmRecord {

    private Long id;

    private Long deviceId;

    private String alarmType;

    private String alarmLevel;

    private String alarmContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    private String alarmData;

    // 处理信息
    private String processStatus = "未处理";

    private String processPerson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    private String processResult;

    private String remarks;

    // 关联对象
    private SmartDevice smartDevice;

    // 系统字段
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 