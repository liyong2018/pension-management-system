package com.example.pension.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Dictionary {
    private Long id;
    private String dictType; // 字典类型：community(所属社区), pensionType(养老类型)
    private String dictCode; // 字典编码
    private String dictLabel; // 字典标签
    private String dictValue; // 字典值
    private Integer sortOrder; // 排序
    private String status; // 状态：ACTIVE(启用), INACTIVE(禁用)
    private String remark; // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 