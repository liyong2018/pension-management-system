package com.example.pension.service;

import com.example.pension.dto.ServiceStaffDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ServiceStaffService {

    /**
     * 创建服务人员
     */
    ServiceStaffDTO create(ServiceStaffDTO serviceStaffDTO);

    /**
     * 根据ID获取服务人员
     */
    ServiceStaffDTO getById(Long id);

    /**
     * 根据工号获取服务人员
     */
    ServiceStaffDTO getByEmployeeId(String employeeId);

    /**
     * 检查工号是否存在
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * 多条件搜索服务人员（分页）
     */
    PageInfo<ServiceStaffDTO> searchByConditions(
            String name, 
            String employeeId, 
            String position, 
            String status, 
            Long organizationId, 
            int pageNum, 
            int pageSize);

    /**
     * 多条件搜索服务人员（分页）- 扩展版本
     */
    PageInfo<ServiceStaffDTO> searchByConditionsExtended(
            String name, 
            String employeeId, 
            String position, 
            String department,
            String workType,
            String workShift,
            String status, 
            Long organizationId, 
            int pageNum, 
            int pageSize);

    /**
     * 更新服务人员信息
     */
    ServiceStaffDTO update(Long id, ServiceStaffDTO serviceStaffDTO);

    /**
     * 删除服务人员
     */
    void delete(Long id);

    /**
     * 批量删除服务人员
     */
    void batchDelete(List<Long> ids);

    /**
     * 变更服务人员状态
     */
    void changeStatus(Long id, String status);

    /**
     * 根据机构ID获取服务人员列表
     */
    List<ServiceStaffDTO> getByOrganizationId(Long organizationId);

    /**
     * 根据职位获取服务人员列表
     */
    List<ServiceStaffDTO> getByPosition(String position);

    /**
     * 根据机构ID统计服务人员数
     */
    Long countByOrganizationId(Long organizationId);

    /**
     * 根据职位统计服务人员数
     */
    Long countByPosition(String position);

    /**
     * 获取服务人员统计数据
     */
    Map<String, Long> getStaffStatistics(Long organizationId);

    /**
     * 获取服务人员排班信息
     */
    Map<String, Object> getStaffSchedule(Long id, String startDate, String endDate);

    /**
     * 导出服务人员列表
     */
    byte[] exportStaffList(
            String name, 
            String employeeId, 
            String position, 
            String status, 
            Long organizationId);

    /**
     * 导入服务人员数据
     */
    Map<String, Object> importStaffData(MultipartFile file) throws Exception;

    /**
     * 获取所有在职服务人员
     */
    List<ServiceStaffDTO> getAllActiveStaff();

    /**
     * 根据部门获取服务人员列表
     */
    List<ServiceStaffDTO> getByDepartment(String department);

    /**
     * 根据工作类型获取服务人员列表
     */
    List<ServiceStaffDTO> getByWorkType(String workType);

    /**
     * 根据班次获取服务人员列表
     */
    List<ServiceStaffDTO> getByWorkShift(String workShift);

    /**
     * 更新服务人员绩效评分
     */
    void updatePerformanceScore(Long id, java.math.BigDecimal score, String comments);

    /**
     * 记录培训信息
     */
    void recordTraining(Long id, String trainingContent, java.time.LocalDate trainingDate);

    /**
     * 更新健康状态
     */
    void updateHealthStatus(Long id, String healthStatus, java.time.LocalDate examDate);

    /**
     * 获取即将到期的证书列表
     */
    List<ServiceStaffDTO> getExpiringCertifications(int days);

    /**
     * 获取需要体检的人员列表
     */
    List<ServiceStaffDTO> getStaffNeedingPhysicalExam(int months);

    /**
     * 获取需要培训的人员列表
     */
    List<ServiceStaffDTO> getStaffNeedingTraining(int months);

    /**
     * 根据技能搜索服务人员
     */
    List<ServiceStaffDTO> searchBySkills(String skills);

    /**
     * 获取生日提醒列表
     */
    List<ServiceStaffDTO> getBirthdayReminders(int days);

    /**
     * 获取入职周年提醒列表
     */
    List<ServiceStaffDTO> getWorkAnniversaryReminders(int days);

    /**
     * 统计各职位人员数量
     */
    Map<String, Long> getPositionStatistics(Long organizationId);

    /**
     * 统计各部门人员数量
     */
    Map<String, Long> getDepartmentStatistics(Long organizationId);

    /**
     * 统计年龄分布
     */
    Map<String, Long> getAgeDistribution(Long organizationId);

    /**
     * 统计学历分布
     */
    Map<String, Long> getEducationDistribution(Long organizationId);

    /**
     * 获取优秀员工列表（根据绩效评分）
     */
    List<ServiceStaffDTO> getTopPerformers(Long organizationId, int limit);

    /**
     * 获取新员工列表（最近入职）
     */
    List<ServiceStaffDTO> getNewEmployees(Long organizationId, int months);

    /**
     * 获取资深员工列表（工作年限长）
     */
    List<ServiceStaffDTO> getSeniorEmployees(Long organizationId, int years);
}