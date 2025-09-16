package com.example.pension.controller;

import com.example.pension.dto.VerifyPushRequest;
import com.example.pension.service.FaceRecognitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 订阅推送控制器
 * 处理人脸识别设备的推送数据
 */
@RestController
@RequestMapping("/Subscribe")
@CrossOrigin(origins = "*")
public class SubscribeController {
    
    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private FaceRecognitionService faceRecognitionService;
    
    /**
     * 处理验证推送数据
     * @param request HTTP请求对象
     * @param requestBody 请求体JSON字符串
     * @return 处理结果
     */
    @PostMapping("/Verify")
    public ResponseEntity<Map<String, Object>> handleVerifyPush(
            HttpServletRequest request,
            @RequestBody String requestBody) {
        
        logger.info("=== 收到/Subscribe/Verify请求 ===");
        logger.info("请求来源IP: {}", request.getRemoteAddr());
        logger.info("Content-Type: {}", request.getContentType());
        logger.info("请求体长度: {}", requestBody != null ? requestBody.length() : 0);
        logger.debug("原始请求体: {}", requestBody);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查请求体是否为空
            if (requestBody == null || requestBody.trim().isEmpty()) {
                logger.warn("请求体为空");
                response.put("success", false);
                response.put("message", "请求体不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 解析JSON数据
            logger.info("开始解析JSON数据...");
            VerifyPushRequest verifyRequest;
            try {
                verifyRequest = objectMapper.readValue(requestBody, VerifyPushRequest.class);
                logger.info("JSON解析成功");
            } catch (Exception e) {
                logger.error("JSON解析失败: {}", e.getMessage(), e);
                response.put("success", false);
                response.put("message", "JSON格式错误: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证操作类型
            String operator = verifyRequest.getOperator();
            logger.info("操作类型: {}", operator);
            
            if (operator == null || operator.trim().isEmpty()) {
                logger.warn("操作类型为空");
                response.put("success", false);
                response.put("message", "操作类型不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查是否为支持的操作类型
            if (!"VerifyPush".equals(operator) && !"RecPush".equals(operator)) {
                logger.warn("不支持的操作类型: {}", operator);
                response.put("success", false);
                response.put("message", "不支持的操作类型: " + operator);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证info对象
            if (verifyRequest.getInfo() == null) {
                logger.warn("info对象为空");
                response.put("success", false);
                response.put("message", "info对象不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 记录关键信息
            logger.info("=== 解析结果 ===");
            logger.info("操作类型: {}", verifyRequest.getOperator());
            logger.info("设备ID: {}", verifyRequest.getInfo().getDeviceId());
            logger.info("人员ID: {}", verifyRequest.getInfo().getPersonId());
            logger.info("人员姓名: {}", verifyRequest.getInfo().getName());
            logger.info("验证状态: {}", verifyRequest.getInfo().getVerifyStatus());
            logger.info("相似度1: {}", verifyRequest.getInfo().getSimilarity1());
            logger.info("创建时间: {}", verifyRequest.getInfo().getCreateTime());
            logger.info("文件索引: {}", verifyRequest.getDwFileIndex());
            logger.info("文件位置: {}", verifyRequest.getDwFilePos());
            logger.info("图片数据长度: {}", 
                verifyRequest.getSanpPic() != null ? verifyRequest.getSanpPic().length() : 0);
            
            // 处理业务逻辑
            logger.info("开始处理业务逻辑...");
            
            // 调用人脸识别服务处理数据
            try {
                if ("VerifyPush".equals(operator)) {
                    logger.info("处理验证推送数据");
                    // 调用人脸识别服务处理记录
                    faceRecognitionService.processRecognitionRecord(verifyRequest);
                    logger.info("人脸识别记录处理完成");
                } else if ("RecPush".equals(operator)) {
                    logger.info("处理记录推送数据");
                    // 调用人脸识别服务处理记录
                    faceRecognitionService.processRecognitionRecord(verifyRequest);
                    logger.info("人脸识别记录处理完成");
                }
            } catch (Exception e) {
                logger.error("处理人脸识别记录时发生异常: {}", e.getMessage(), e);
                response.put("success", false);
                response.put("message", "处理人脸识别记录失败: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            logger.info("业务处理完成");
            
            // 返回成功响应
            response.put("success", true);
            response.put("message", "数据处理成功");
            response.put("operator", operator);
            response.put("deviceId", verifyRequest.getInfo().getDeviceId());
            response.put("personId", verifyRequest.getInfo().getPersonId());
            
            logger.info("=== 请求处理完成，返回成功响应 ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("处理请求时发生异常: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 心跳接口
     * @return 心跳响应
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat() {
        logger.debug("收到心跳请求");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "心跳正常");
        response.put("timestamp", System.currentTimeMillis());
        
        logger.debug("心跳响应发送完成");
        return ResponseEntity.ok(response);
    }
}