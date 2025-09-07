package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuPermissionDTO {

    private Long id;

    private Long parentId;

    private String name;

    private String type; // MENU, BUTTON, API

    private String permissionKey;

    private String routePath;

    private String componentPath;

    private String icon;

    private Integer sortOrder = 0;

    private Boolean isVisible = true;

    private Boolean status = true;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 子菜单/权限列表
    private List<MenuPermissionDTO> children;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public List<MenuPermissionDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuPermissionDTO> children) {
        this.children = children;
    }
}