package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleDTO {

    private Long id;

    private String roleName;

    private String roleKey;

    private String description;

    private String status = "1"; // 1:启用 0:禁用

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 权限ID列表（用于前端传递）
    private List<Long> permissionIds;

    // 权限信息列表（用于前端展示）
    private List<MenuPermissionDTO> permissions;

    // Getter and Setter methods
    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }
}