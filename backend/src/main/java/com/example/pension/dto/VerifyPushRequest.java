package com.example.pension.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 验证推送请求数据模型
 * 对应完整的JSON结构
 */
public class VerifyPushRequest {
    
    @JsonProperty("operator")
    private String operator;
    
    @JsonProperty("info")
    private VerifyInfo info;
    
    @JsonProperty("dwFileIndex")
    private String dwFileIndex;
    
    @JsonProperty("dwFilePos")
    private String dwFilePos;
    
    @JsonProperty("SanpPic")
    private String sanpPic;
    
    // Getters and Setters
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public VerifyInfo getInfo() {
        return info;
    }
    
    public void setInfo(VerifyInfo info) {
        this.info = info;
    }
    
    public String getDwFileIndex() {
        return dwFileIndex;
    }
    
    public void setDwFileIndex(String dwFileIndex) {
        this.dwFileIndex = dwFileIndex;
    }
    
    public String getDwFilePos() {
        return dwFilePos;
    }
    
    public void setDwFilePos(String dwFilePos) {
        this.dwFilePos = dwFilePos;
    }
    
    public String getSanpPic() {
        return sanpPic;
    }
    
    public void setSanpPic(String sanpPic) {
        this.sanpPic = sanpPic;
    }
    
    @Override
    public String toString() {
        return "VerifyPushRequest{" +
                "operator='" + operator + '\'' +
                ", info=" + info +
                ", dwFileIndex='" + dwFileIndex + '\'' +
                ", dwFilePos='" + dwFilePos + '\'' +
                ", sanpPic='" + (sanpPic != null ? sanpPic.substring(0, Math.min(50, sanpPic.length())) + "..." : "null") + '\'' +
                '}';
    }
}