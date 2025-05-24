package com.example.pension.controller;

import com.example.pension.model.Volunteer;
import com.example.pension.service.VolunteerService;
import com.example.pension.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin(origins = "*")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 分页查询志愿者列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getVolunteerList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return volunteerService.getVolunteerList(name, phone, status, page, pageSize);
    }

    /**
     * 根据ID获取志愿者详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Volunteer> getVolunteerById(@PathVariable Long id) {
        return volunteerService.getVolunteerById(id);
    }

    /**
     * 创建志愿者
     */
    @PostMapping
    public ApiResponse<Volunteer> createVolunteer(@RequestBody Volunteer volunteer) {
        return volunteerService.createVolunteer(volunteer);
    }

    /**
     * 更新志愿者信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Volunteer> updateVolunteer(@PathVariable Long id, @RequestBody Volunteer volunteer) {
        return volunteerService.updateVolunteer(id, volunteer);
    }

    /**
     * 删除志愿者
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVolunteer(@PathVariable Long id) {
        return volunteerService.deleteVolunteer(id);
    }

    /**
     * 更新志愿者状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateVolunteerStatus(@PathVariable Long id, @RequestParam String status) {
        return volunteerService.updateVolunteerStatus(id, status);
    }

    /**
     * 更新志愿者服务时长和积分
     */
    @PutMapping("/{id}/service-stats")
    public ApiResponse<Void> updateServiceStats(
            @PathVariable Long id,
            @RequestParam(required = false) Double hours,
            @RequestParam(required = false) Integer points) {
        return volunteerService.updateServiceStats(id, hours, points);
    }

    /**
     * 获取优秀志愿者排行榜
     */
    @GetMapping("/top")
    public ApiResponse<List<Volunteer>> getTopVolunteers() {
        return volunteerService.getTopVolunteers();
    }

    /**
     * 获取志愿者统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getVolunteerStats() {
        return volunteerService.getVolunteerStats();
    }
} 