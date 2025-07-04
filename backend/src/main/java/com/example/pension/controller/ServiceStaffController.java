package com.example.pension.controller;

import com.example.pension.dto.ServiceStaffDTO;
import com.example.pension.service.ServiceStaffService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/service-staff")
@CrossOrigin(origins = "*")
public class ServiceStaffController {

    @Autowired
    private ServiceStaffService serviceStaffService;

    /**
     * 获取服务人员列表（分页）
     * 只查询机构下面的人员
     */
    @GetMapping
    public ResponseEntity<PageInfo<ServiceStaffDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long organizationId) {
        
        PageInfo<ServiceStaffDTO> result = serviceStaffService.searchByConditions(
                name, employeeId, position, status, organizationId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多条件搜索服务人员
     */
    @GetMapping("/search")
    public ResponseEntity<PageInfo<ServiceStaffDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String workShift,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageInfo<ServiceStaffDTO> result = serviceStaffService.searchByConditionsExtended(
                name, employeeId, position, department, workType, workShift, status, organizationId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取服务人员统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStaffStats(
            @RequestParam(required = false) Long organizationId) {
        Map<String, Long> result = serviceStaffService.getStaffStatistics(organizationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个服务人员详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceStaffDTO> getById(@PathVariable Long id) {
        ServiceStaffDTO result = serviceStaffService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据工号获取服务人员
     */
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<ServiceStaffDTO> getByEmployeeId(@PathVariable String employeeId) {
        ServiceStaffDTO result = serviceStaffService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新服务人员
     */
    @PostMapping
    public ResponseEntity<ServiceStaffDTO> create(@RequestBody ServiceStaffDTO serviceStaffDTO) {
        // 验证工号唯一性
        if (serviceStaffService.existsByEmployeeId(serviceStaffDTO.getEmployeeId())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "工号已存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        ServiceStaffDTO result = serviceStaffService.create(serviceStaffDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新服务人员信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceStaffDTO> update(
            @PathVariable Long id,
            @RequestBody ServiceStaffDTO serviceStaffDTO) {
        ServiceStaffDTO result = serviceStaffService.update(id, serviceStaffDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除服务人员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceStaffService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除服务人员
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Void> batchDelete(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        serviceStaffService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 变更服务人员状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        serviceStaffService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据机构ID获取服务人员列表
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<ServiceStaffDTO>> getByOrganizationId(@PathVariable Long organizationId) {
        List<ServiceStaffDTO> result = serviceStaffService.getByOrganizationId(organizationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据职位获取服务人员列表
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<ServiceStaffDTO>> getByPosition(@PathVariable String position) {
        List<ServiceStaffDTO> result = serviceStaffService.getByPosition(position);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据机构ID统计服务人员数
     */
    @GetMapping("/statistics/organization/{organizationId}")
    public ResponseEntity<Long> countByOrganizationId(@PathVariable Long organizationId) {
        Long result = serviceStaffService.countByOrganizationId(organizationId);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据职位统计服务人员数
     */
    @GetMapping("/statistics/position/{position}")
    public ResponseEntity<Long> countByPosition(@PathVariable String position) {
        Long result = serviceStaffService.countByPosition(position);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取服务人员排班信息
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<Map<String, Object>> getStaffSchedule(
            @PathVariable Long id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> result = serviceStaffService.getStaffSchedule(id, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 导出服务人员列表
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStaffList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long organizationId) {
        
        byte[] result = serviceStaffService.exportStaffList(name, employeeId, position, status, organizationId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=service_staff_list.xlsx")
                .body(result);
    }

    /**
     * 导入服务人员数据
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importStaffData(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            Map<String, Object> result = serviceStaffService.importStaffData(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("导入服务人员数据失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "导入失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 获取当前用户所在机构的服务人员列表
     */
    @GetMapping("/my-organization")
    public ResponseEntity<PageInfo<ServiceStaffDTO>> getMyOrganizationStaff(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status) {
        
        // 获取当前登录用户的机构ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 这里需要根据当前用户获取其机构ID
        // 实际实现中需要从用户信息中获取organizationId
        Long currentUserOrganizationId = getCurrentUserOrganizationId();
        
        PageInfo<ServiceStaffDTO> result = serviceStaffService.searchByConditions(
                name, employeeId, position, status, currentUserOrganizationId, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前用户的机构ID
     * 这是一个辅助方法，实际实现中需要根据具体的用户管理逻辑来获取
     */
    private Long getCurrentUserOrganizationId() {
        // 这里应该根据当前登录用户获取其机构ID
        // 可以通过注入SystemUserService来获取用户信息
        // 暂时返回null，表示查询所有机构的人员
        return null;
    }
}