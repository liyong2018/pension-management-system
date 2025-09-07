package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemUserDTO {

    private Long id;

    private String username;

    private String password; // 用于接收前端传入的明文密码

    private String fullName;

    private String email;

    private String phone;

    private Long organizationId;

    private String organizationName;

    private Long serviceStaffId;

    private Boolean isAdmin = false;

    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 角色ID列表（用于前端传递）
    private List<Long> roleIds;

    // 角色信息列表（用于前端展示）
    private List<RoleDTO> roles;
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public Long getServiceStaffId() {
        return serviceStaffId;
    }
    
    public void setServiceStaffId(Long serviceStaffId) {
        this.serviceStaffId = serviceStaffId;
    }
    
    public List<Long> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
    
    public List<RoleDTO> getRoles() {
        return roles;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}