package com.example.pension.controller;

import com.example.pension.dto.SmartDeviceDTO;
import com.example.pension.service.SmartDeviceService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart-devices")
@CrossOrigin(origins = "*")
public class SmartDeviceController {

    @Autowired
    private SmartDeviceService smartDeviceService;

    /**
     * 获取设备列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<SmartDeviceDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<SmartDeviceDTO> result = smartDeviceService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索设备
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<SmartDeviceDTO>> search(
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String deviceStatus,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<SmartDeviceDTO> result = smartDeviceService.searchByMultipleConditions(
                deviceName, deviceType, deviceStatus, elderlyId, organizationId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个设备详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SmartDeviceDTO> getById(@PathVariable Long id) {
        SmartDeviceDTO result = smartDeviceService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新设备
     */
    @PostMapping
    public ResponseEntity<SmartDeviceDTO> create(@RequestBody SmartDeviceDTO smartDeviceDTO) {
        SmartDeviceDTO result = smartDeviceService.create(smartDeviceDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新设备信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<SmartDeviceDTO> update(
            @PathVariable Long id,
            @RequestBody SmartDeviceDTO smartDeviceDTO) {
        SmartDeviceDTO result = smartDeviceService.update(id, smartDeviceDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        smartDeviceService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除设备
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        smartDeviceService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取指定老人的设备列表
     */
    @GetMapping("/elderly/{elderlyId}")
    public ResponseEntity<List<SmartDeviceDTO>> getByElderlyId(@PathVariable Long elderlyId) {
        List<SmartDeviceDTO> result = smartDeviceService.getByElderlyId(elderlyId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取指定机构的设备列表
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<SmartDeviceDTO>> getByOrganizationId(@PathVariable Long organizationId) {
        List<SmartDeviceDTO> result = smartDeviceService.getByOrganizationId(organizationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取设备类型统计
     */
    @GetMapping("/statistics/type")
    public ResponseEntity<Map<String, Long>> getDeviceTypeStatistics() {
        Map<String, Long> result = smartDeviceService.getDeviceTypeStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取设备状态统计
     */
    @GetMapping("/statistics/status")
    public ResponseEntity<Map<String, Long>> getDeviceStatusStatistics() {
        Map<String, Long> result = smartDeviceService.getDeviceStatusStatistics();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取需要维护的设备列表
     */
    @GetMapping("/maintenance/needed")
    public ResponseEntity<List<SmartDeviceDTO>> getDevicesNeedMaintenance() {
        List<SmartDeviceDTO> result = smartDeviceService.getDevicesNeedMaintenance();
        return ResponseEntity.ok(result);
    }

    /**
     * 更新设备状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateDeviceStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        smartDeviceService.updateDeviceStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新设备电量
     */
    @PutMapping("/{id}/battery")
    public ResponseEntity<Void> updateDeviceBatteryLevel(
            @PathVariable Long id,
            @RequestParam Integer batteryLevel) {
        smartDeviceService.updateDeviceBatteryLevel(id, batteryLevel);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新设备实时数据
     */
    @PutMapping("/{id}/realtime-data")
    public ResponseEntity<Void> updateDeviceRealTimeData(
            @PathVariable Long id,
            @RequestBody String realTimeData) {
        smartDeviceService.updateDeviceRealTimeData(id, realTimeData);
        return ResponseEntity.ok().build();
    }
} 