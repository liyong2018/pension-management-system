package com.example.pension.service;

import com.example.pension.mapper.VolunteerMapper;
import com.example.pension.model.Volunteer;
import com.example.pension.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VolunteerService {

    @Autowired
    private VolunteerMapper volunteerMapper;

    /**
     * 分页查询志愿者列表
     */
    public ApiResponse<Map<String, Object>> getVolunteerList(String name, String phone, String status, 
                                                           int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            List<Volunteer> volunteers = volunteerMapper.findByConditions(name, phone, status, offset, pageSize);
            int total = volunteerMapper.countByConditions(name, phone, status);

            Map<String, Object> result = new HashMap<>();
            result.put("volunteers", volunteers);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("查询志愿者列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取志愿者详情
     */
    public ApiResponse<Volunteer> getVolunteerById(Long id) {
        try {
            Volunteer volunteer = volunteerMapper.findById(id);
            if (volunteer == null) {
                return ApiResponse.error("志愿者不存在");
            }
            return ApiResponse.success(volunteer);
        } catch (Exception e) {
            return ApiResponse.error("获取志愿者详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建志愿者
     */
    @Transactional
    public ApiResponse<Volunteer> createVolunteer(Volunteer volunteer) {
        try {
            // 验证必填字段
            if (!StringUtils.hasText(volunteer.getName())) {
                return ApiResponse.error("姓名不能为空");
            }
            if (!StringUtils.hasText(volunteer.getPhone())) {
                return ApiResponse.error("联系方式不能为空");
            }

            // 检查手机号是否已存在
            if (volunteerMapper.findByPhone(volunteer.getPhone()) != null) {
                return ApiResponse.error("该手机号已被注册");
            }

            // 检查身份证号是否已存在
            if (StringUtils.hasText(volunteer.getIdCardNumber()) && 
                volunteerMapper.findByIdCardNumber(volunteer.getIdCardNumber()) != null) {
                return ApiResponse.error("该身份证号已被注册");
            }

            // 设置默认值
            if (volunteer.getStatus() == null) {
                volunteer.setStatus("ACTIVE");
            }
            if (volunteer.getTotalServiceHours() == null) {
                volunteer.setTotalServiceHours(BigDecimal.ZERO);
            }
            if (volunteer.getPoints() == null) {
                volunteer.setPoints(0);
            }
            if (volunteer.getRegistrationDate() == null) {
                volunteer.setRegistrationDate(new Date(System.currentTimeMillis()));
            }

            volunteerMapper.insert(volunteer);
            return ApiResponse.success(volunteer);
        } catch (Exception e) {
            return ApiResponse.error("创建志愿者失败: " + e.getMessage());
        }
    }

    /**
     * 更新志愿者信息
     */
    @Transactional
    public ApiResponse<Volunteer> updateVolunteer(Long id, Volunteer volunteer) {
        try {
            Volunteer existingVolunteer = volunteerMapper.findById(id);
            if (existingVolunteer == null) {
                return ApiResponse.error("志愿者不存在");
            }

            // 检查手机号是否被其他志愿者使用
            if (StringUtils.hasText(volunteer.getPhone()) && !volunteer.getPhone().equals(existingVolunteer.getPhone())) {
                Volunteer phoneCheck = volunteerMapper.findByPhone(volunteer.getPhone());
                if (phoneCheck != null && !phoneCheck.getId().equals(id)) {
                    return ApiResponse.error("该手机号已被其他志愿者注册");
                }
            }

            // 检查身份证号是否被其他志愿者使用
            if (StringUtils.hasText(volunteer.getIdCardNumber()) && 
                !volunteer.getIdCardNumber().equals(existingVolunteer.getIdCardNumber())) {
                Volunteer idCardCheck = volunteerMapper.findByIdCardNumber(volunteer.getIdCardNumber());
                if (idCardCheck != null && !idCardCheck.getId().equals(id)) {
                    return ApiResponse.error("该身份证号已被其他志愿者注册");
                }
            }

            volunteer.setId(id);
            volunteerMapper.update(volunteer);

            Volunteer updatedVolunteer = volunteerMapper.findById(id);
            return ApiResponse.success(updatedVolunteer);
        } catch (Exception e) {
            return ApiResponse.error("更新志愿者信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除志愿者
     */
    @Transactional
    public ApiResponse<Void> deleteVolunteer(Long id) {
        try {
            Volunteer volunteer = volunteerMapper.findById(id);
            if (volunteer == null) {
                return ApiResponse.error("志愿者不存在");
            }

            volunteerMapper.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除志愿者失败: " + e.getMessage());
        }
    }

    /**
     * 更新志愿者状态
     */
    @Transactional
    public ApiResponse<Void> updateVolunteerStatus(Long id, String status) {
        try {
            Volunteer volunteer = volunteerMapper.findById(id);
            if (volunteer == null) {
                return ApiResponse.error("志愿者不存在");
            }

            // 验证状态值
            if (!isValidStatus(status)) {
                return ApiResponse.error("无效的状态值");
            }

            volunteerMapper.updateStatus(id, status);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("更新志愿者状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新志愿者服务时长和积分
     */
    @Transactional
    public ApiResponse<Void> updateServiceStats(Long id, Double hours, Integer points) {
        try {
            Volunteer volunteer = volunteerMapper.findById(id);
            if (volunteer == null) {
                return ApiResponse.error("志愿者不存在");
            }

            if (hours != null && hours < 0) {
                return ApiResponse.error("服务时长不能为负数");
            }
            if (points != null && points < 0) {
                return ApiResponse.error("积分不能为负数");
            }

            volunteerMapper.updateServiceStats(id, hours != null ? hours : 0, points != null ? points : 0);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("更新服务统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取优秀志愿者排行榜
     */
    public ApiResponse<List<Volunteer>> getTopVolunteers() {
        try {
            List<Volunteer> topVolunteers = volunteerMapper.findTopVolunteers();
            return ApiResponse.success(topVolunteers);
        } catch (Exception e) {
            return ApiResponse.error("获取优秀志愿者排行榜失败: " + e.getMessage());
        }
    }

    /**
     * 获取志愿者统计信息
     */
    public ApiResponse<Map<String, Object>> getVolunteerStats() {
        try {
            int activeCount = volunteerMapper.countByConditions(null, null, "ACTIVE");
            int inactiveCount = volunteerMapper.countByConditions(null, null, "INACTIVE");
            int pendingCount = volunteerMapper.countByConditions(null, null, "PENDING_APPROVAL");
            int totalCount = volunteerMapper.countByConditions(null, null, null);

            Map<String, Object> stats = new HashMap<>();
            stats.put("activeCount", activeCount);
            stats.put("inactiveCount", inactiveCount);
            stats.put("pendingCount", pendingCount);
            stats.put("totalCount", totalCount);

            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取志愿者统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 验证状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "ACTIVE".equals(status) || "INACTIVE".equals(status) || "PENDING_APPROVAL".equals(status);
    }
} 