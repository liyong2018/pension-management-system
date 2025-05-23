package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceAlarmRecordDTO {

    private Long id;

    private Long deviceId;

    private String deviceName; // 设备名称

    private String deviceCode; // 设备编号

    private String alarmType;

    private String alarmLevel;

    private String alarmContent;

    private LocalDateTime alarmTime;

    private String alarmData;

    // 处理信息
    private String processStatus;

    private String processPerson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    private String processResult;

    private String remarks;

    // 系统字段
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 