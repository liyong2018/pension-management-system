package com.example.pension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictionaryDTO {
    private Long id;
    
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 50, message = "字典类型长度不能超过50个字符")
    private String dictType;
    
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 100, message = "字典编码长度不能超过100个字符")
    private String dictCode;
    
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;
    
    @NotBlank(message = "字典值不能为空")
    @Size(max = 200, message = "字典值长度不能超过200个字符")
    private String dictValue;
    
    @Min(value = 0, message = "排序不能小于0")
    private Integer sortOrder;
    
    @Size(max = 20, message = "状态长度不能超过20个字符")
    private String status;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getter and Setter methods
    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }
}