package com.example.pension.controller;

import com.example.pension.dto.DashboardStatsDTO;
import com.example.pension.service.DashboardService;
import com.example.pension.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页统计数据控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近告警数据
     */
    @GetMapping("/alarms/recent")
    public ApiResponse<List<DashboardService.AlarmDataDTO>> getRecentAlarms() {
        try {
            List<DashboardService.AlarmDataDTO> alarms = dashboardService.getRecentAlarms();
            return ApiResponse.success(alarms);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取告警数据失败: " + e.getMessage());
        }
    }
} 