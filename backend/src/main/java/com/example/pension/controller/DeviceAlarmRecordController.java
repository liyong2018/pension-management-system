package com.example.pension.controller;

import com.example.pension.dto.DeviceAlarmRecordDTO;
import com.example.pension.service.DeviceAlarmRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device-alarms")
@CrossOrigin(origins = "*")
public class DeviceAlarmRecordController {

    @Autowired
    private DeviceAlarmRecordService deviceAlarmRecordService;

    /**
     * 获取告警记录列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<DeviceAlarmRecordDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<DeviceAlarmRecordDTO> result = deviceAlarmRecordService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索告警记录
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<DeviceAlarmRecordDTO>> search(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String alarmLevel,
            @RequestParam(required = false) String processStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<DeviceAlarmRecordDTO> result = deviceAlarmRecordService.searchByMultipleConditions(
                deviceId, alarmType, alarmLevel, processStatus, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个告警记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeviceAlarmRecordDTO> getById(@PathVariable Long id) {
        DeviceAlarmRecordDTO result = deviceAlarmRecordService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建告警记录
     */
    @PostMapping
    public ResponseEntity<DeviceAlarmRecordDTO> create(@RequestBody DeviceAlarmRecordDTO deviceAlarmRecordDTO) {
        DeviceAlarmRecordDTO result = deviceAlarmRecordService.create(deviceAlarmRecordDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新告警记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeviceAlarmRecordDTO> update(
            @PathVariable Long id,
            @RequestBody DeviceAlarmRecordDTO deviceAlarmRecordDTO) {
        DeviceAlarmRecordDTO result = deviceAlarmRecordService.update(id, deviceAlarmRecordDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除告警记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceAlarmRecordService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除告警记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        deviceAlarmRecordService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取指定设备的告警记录
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceAlarmRecordDTO>> getByDeviceId(@PathVariable Long deviceId) {
        List<DeviceAlarmRecordDTO> result = deviceAlarmRecordService.getByDeviceId(deviceId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取告警类型统计
     */
    @GetMapping("/statistics/type")
    public ResponseEntity<Map<String, Long>> getAlarmTypeStatistics() {
        Map<String, Long> result = deviceAlarmRecordService.getAlarmTypeStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取告警级别统计
     */
    @GetMapping("/statistics/level")
    public ResponseEntity<Map<String, Long>> getAlarmLevelStatistics() {
        Map<String, Long> result = deviceAlarmRecordService.getAlarmLevelStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取处理状态统计
     */
    @GetMapping("/statistics/status")
    public ResponseEntity<Map<String, Long>> getProcessStatusStatistics() {
        Map<String, Long> result = deviceAlarmRecordService.getProcessStatusStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取未处理告警数量
     */
    @GetMapping("/unprocessed/count")
    public ResponseEntity<Long> getUnprocessedAlarmCount() {
        Long result = deviceAlarmRecordService.getUnprocessedAlarmCount();
        return ResponseEntity.ok(result);
    }

    /**
     * 处理告警
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<Void> processAlarm(
            @PathVariable Long id,
            @RequestParam String processPerson,
            @RequestParam String processResult,
            @RequestParam(required = false) String remarks) {
        deviceAlarmRecordService.processAlarm(id, processPerson, processResult, remarks);
        return ResponseEntity.ok().build();
    }
} 