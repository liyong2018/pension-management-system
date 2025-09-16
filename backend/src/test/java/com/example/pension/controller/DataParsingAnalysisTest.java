package com.example.pension.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * 分析OneNET推送数据解析问题
 */
public class DataParsingAnalysisTest {

    @Test
    public void analyzeOneNetDataStructure() throws Exception {
        // 实际OneNET推送的数据结构（来自日志）
        String oneNetData = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"离线\"}}},\"deviceName\":\"860870050503148\"}";
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(oneNetData);
        
        System.out.println("=== OneNET数据结构分析 ===");
        System.out.println("完整数据: " + oneNetData);
        System.out.println();
        
        System.out.println("根节点字段:");
        rootNode.fieldNames().forEachRemaining(fieldName -> {
            System.out.println("  " + fieldName + ": " + rootNode.get(fieldName));
        });
        
        System.out.println();
        System.out.println("data节点内容:");
        JsonNode dataNode = rootNode.get("data");
        if (dataNode != null) {
            System.out.println("  id: " + dataNode.get("id"));
            JsonNode paramsNode = dataNode.get("params");
            if (paramsNode != null) {
                System.out.println("  params字段:");
                paramsNode.fieldNames().forEachRemaining(paramName -> {
                    JsonNode paramValue = paramsNode.get(paramName);
                    System.out.println("    " + paramName + ":");
                    System.out.println("      time: " + paramValue.get("time"));
                    System.out.println("      value: " + paramValue.get("value"));
                });
            }
        }
        
        System.out.println();
        System.out.println("=== 问题分析 ===");
        System.out.println("1. OneNET推送的数据结构与我们的OneNetPushData<WatchData>模型不匹配");
        System.out.println("2. 实际数据包含notifyType、productId、messageType等字段，但我们的模型期望msgId、deviceId、timestamp等");
        System.out.println("3. 设备数据在data.params中，格式为{fieldName: {time: xxx, value: xxx}}");
        System.out.println("4. 离线状态在Wear字段中，值为'离线'");
        System.out.println("5. 需要创建新的数据模型来正确解析这种格式");
    }
}