package com.example.pension.controller;

import com.example.pension.model.face.PushData;
import com.example.pension.model.face.RecInfo;
import com.example.pension.model.face.StrangerInfo;
import com.example.pension.model.face.HeartBeatInfo;
import com.example.pension.model.face.Response;
import com.example.pension.service.FaceRecognitionService;
import com.example.pension.service.SmartDeviceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 人脸识别推送接收控制器
 * 用于接收人脸识别一体机的HTTP订阅推送数据
 */
@RestController
@RequestMapping("/api/face")
@CrossOrigin(origins = "*")
public class FacePushController {
    
    private static final Logger logger = LoggerFactory.getLogger(FacePushController.class);
    
    @Autowired
    private FaceRecognitionService faceRecognitionService;
    
    /**
     * 接收认证记录推送（对应设备配置的 VerifyURL: /api/face/push）
     * @param pushData 推送数据
     * @return 响应结果
     */
    @PostMapping("/push")
    public ResponseEntity<Response> receiveVerify(@RequestBody PushData<RecInfo> pushData) {
        try {
            logger.info("接收到认证记录推送，操作类型: {}", pushData.getOperator());
            
            // 1. 校验推送类型（兼容 RecPush 与 VerifyPush）
            String op = pushData.getOperator();
            if (!"RecPush".equals(op) && !"VerifyPush".equals(op)) {
                logger.warn("无效的认证记录数据，操作类型: {}", pushData.getOperator());
                return ResponseEntity.badRequest().body(new Response(400, "无效的认证记录数据"));
            }
            
            // 2. 解析认证信息
            RecInfo info = pushData.getInfo();
            if (info == null) {
                logger.warn("认证记录数据为空");
                return ResponseEntity.badRequest().body(new Response(400, "认证记录数据为空"));
            }
            
            logger.info("=== 接收到认证记录 ===");
            logger.info("人员ID: {}", info.getCustomId());
            logger.info("认证结果: {} (1=允许，2=拒绝，22=待核验)", info.getVerifyStatus());
            logger.info("相似度: {}%", info.getSimilarity1());
            logger.info("记录ID: {}", info.getRecordID());
            logger.info("设备ID: {}", info.getFacesluiceId());
            
            // 3. 处理认证记录数据
            faceRecognitionService.processRecognitionRecord(info);
            
            // 4. 断点续传：返回确认响应（必须在10秒内）
            return ResponseEntity.ok(new Response(200, "OK"));
            
        } catch (Exception e) {
            logger.error("处理认证记录推送失败", e);
            return ResponseEntity.internalServerError().body(new Response(500, "处理失败: " + e.getMessage()));
        }
    }
    
    /**
     * 接收陌生人抓拍推送（对应设备配置的 SnapURL: /api/face-push/snap）
     * @param pushData 推送数据
     * @return 响应结果
     */
    @PostMapping("/snap")
    public ResponseEntity<Response> receiveSnap(@RequestBody PushData<StrangerInfo> pushData) {
        try {
            logger.info("接收到陌生人抓拍推送，操作类型: {}", pushData.getOperator());
            
            // 1. 校验推送类型
            if (!"StrSnapPush".equals(pushData.getOperator())) {
                logger.warn("无效的陌生人抓拍数据，操作类型: {}", pushData.getOperator());
                return ResponseEntity.badRequest().body(new Response(400, "无效的陌生人抓拍数据"));
            }
            
            // 2. 解析陌生人信息
            StrangerInfo info = pushData.getInfo();
            if (info == null) {
                logger.warn("陌生人抓拍数据为空");
                return ResponseEntity.badRequest().body(new Response(400, "陌生人抓拍数据为空"));
            }
            
            logger.info("\n=== 接收到陌生人抓拍 ===");
            logger.info("抓拍时间: {}", info.getTime());
            logger.info("抓拍ID: {}", info.getSnapID());
            logger.info("设备ID: {}", info.getFacesluiceId());
            
            // 3. 处理陌生人抓拍数据
            faceRecognitionService.processStrangerRecord(info);
            
            // 4. 断点续传：返回确认响应
            return ResponseEntity.ok(new Response(200, "OK"));
            
        } catch (Exception e) {
            logger.error("处理陌生人抓拍推送失败", e);
            return ResponseEntity.internalServerError().body(new Response(500, "处理失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Response> health() {
        return ResponseEntity.ok(new Response(200, "Face Push Service is running"));
    }
}

/**
 * 设备心跳和订阅接口控制器
 * 用于匹配人脸识别设备的默认URL配置
 */
@RestController
@RequestMapping("/Subscribe")
@CrossOrigin(origins = "*")
class SubscribeController {
    
    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);
    
    @Autowired
    private SmartDeviceService smartDeviceService;
    
    @Autowired
    private FaceRecognitionService faceRecognitionService;
    
    /**
     * 心跳接口 - 对应设备配置的心跳URL
     * @param request HTTP请求对象，用于获取客户端IP
     * @return 心跳响应
     */
    @GetMapping("/heartbeat")
    public ResponseEntity<Response> heartbeat(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("收到设备心跳请求，客户端IP: {}", clientIp);
        
        // 更新设备最后通信时间
        try {
            smartDeviceService.updateLastCommunicationTime(clientIp);
            logger.info("已更新设备 {} 的最后通信时间", clientIp);
        } catch (Exception e) {
            logger.warn("更新设备通信时间失败，IP: {}, 错误: {}", clientIp, e.getMessage());
        }
        
        return ResponseEntity.ok(new Response(200, "OK"));
    }
    
    @PostMapping("/heartbeat")
    public ResponseEntity<Response> heartbeatPost(@RequestBody String rawBody, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        
        // 详细调试日志 - 第一时间打印原始数据
        logger.info("\n========== /Subscribe/heartbeat 接口收到HTTP推送请求 ==========");
        logger.info("请求时间: {}", java.time.LocalDateTime.now());
        logger.info("客户端IP: {}", clientIp);
        logger.info("请求方法: POST");
        logger.info("请求路径: /Subscribe/heartbeat");
        
        // 打印请求头信息
        logger.info("\n--- 请求头信息 ---");
        logger.info("Content-Type: {}", request.getHeader("Content-Type"));
        logger.info("Content-Length: {}", request.getHeader("Content-Length"));
        logger.info("User-Agent: {}", request.getHeader("User-Agent"));
        logger.info("Accept: {}", request.getHeader("Accept"));
        
        // 打印原始请求体
        logger.info("\n--- 原始请求体内容 ---");
        logger.info("原始请求体长度: {} 字符", rawBody != null ? rawBody.length() : 0);
        logger.info("原始请求体内容: {}", rawBody);
        
        try {
            // JSON解析前日志
            logger.info("\n--- 开始JSON解析 ---");
            logger.info("准备解析JSON为PushData<HeartBeatInfo>对象...");
            
            // 手动解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            PushData<HeartBeatInfo> pushData;
            
            try {
                // 先解析为通用的PushData
                TypeReference<PushData<HeartBeatInfo>> typeRef = new TypeReference<PushData<HeartBeatInfo>>() {};
                pushData = objectMapper.readValue(rawBody, typeRef);
                logger.info("✅ JSON解析成功");
                logger.info("解析后的对象: {}", pushData != null ? pushData.toString() : "null");
            } catch (Exception parseException) {
                logger.error("❌ JSON解析失败");
                logger.error("解析错误详情: {}", parseException.getMessage());
                logger.error("解析异常堆栈:", parseException);
                return ResponseEntity.badRequest().body(new Response(400, "JSON解析失败: " + parseException.getMessage()));
            }
            
            logger.info("\n--- 开始业务逻辑处理 ---");
            
            // 1. 校验推送类型
            logger.info("步骤1: 校验推送类型");
            if (!"HeartBeat".equals(pushData.getOperator())) {
                logger.warn("❌ 校验失败 - 无效的心跳数据，操作类型: {}", pushData.getOperator());
                logger.warn("期望的操作类型: HeartBeat");
                return ResponseEntity.badRequest().body(new Response(400, "无效的心跳数据"));
            }
            logger.info("✅ 校验通过 - 操作类型正确: {}", pushData.getOperator());
            
            // 2. 解析心跳信息
            logger.info("步骤2: 解析心跳信息");
            HeartBeatInfo info = pushData.getInfo();
            if (info == null) {
                logger.warn("❌ 解析失败 - 心跳数据为空");
                return ResponseEntity.badRequest().body(new Response(400, "心跳数据为空"));
            }
            logger.info("✅ 解析成功 - 心跳信息不为空");
            
            logger.info("\n=== 心跳包详细信息 ===");
            logger.info("设备ID (DeviceID): {}", info.getDeviceId());
            logger.info("设备IP: {}", info.getIp());
            logger.info("MAC地址: {}", info.getMacAddr());
            logger.info("WiFi IP: {}", info.getWifiIp());
            logger.info("心跳时间: {}", info.getTime());
            logger.info("兼容字段 facesluiceId: {}", info.getFacesluiceId());
            
            // 3. 更新设备最后通信时间并存储心跳数据
            logger.info("步骤3: 更新设备通信时间和存储心跳数据");
            // 优先使用facesluiceId查找设备，如果找不到则使用IP地址
            smartDeviceService.updateLastCommunicationTimeWithHeartbeatByFacesluiceId(info.getFacesluiceId(), clientIp, pushData);
            logger.info("✅ 已更新设备 facesluiceId={} IP={} 的最后通信时间和心跳数据", info.getFacesluiceId(), clientIp);
            
            logger.info("\n========== 心跳处理完成，返回成功响应 ==========");
            
        } catch (Exception e) {
            logger.error("\n❌ 处理心跳数据失败");
            logger.error("客户端IP: {}", clientIp);
            logger.error("错误信息: {}", e.getMessage());
            logger.error("错误堆栈:", e);
            return ResponseEntity.internalServerError().body(new Response(500, "处理失败: " + e.getMessage()));
        }
        
        return ResponseEntity.ok(new Response(200, "OK"));
    }
    
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 认证记录推送接口 - 匹配设备默认配置
     * @param pushData 推送数据
     * @return 响应结果
     */
    @PostMapping("/Verify")
     public ResponseEntity<Response> recPush(@RequestBody String rawBody, HttpServletRequest request) {
         try {
             String clientIp = getClientIpAddress(request);
             logger.info("\n========== /Subscribe/Verify 接口收到HTTP推送请求 ==========");
             logger.info("请求时间: {}", java.time.LocalDateTime.now());
             logger.info("客户端IP: {}", clientIp);
             logger.info("请求方法: POST");
             logger.info("请求路径: /Subscribe/Verify");

             // 打印请求头信息
             logger.info("\n--- 请求头信息 ---");
             logger.info("Content-Type: {}", request.getHeader("Content-Type"));
             logger.info("Content-Length: {}", request.getHeader("Content-Length"));
             logger.info("User-Agent: {}", request.getHeader("User-Agent"));
             logger.info("Accept: {}", request.getHeader("Accept"));

             // 打印原始请求体
             logger.info("\n--- 原始请求体内容 ---");
             logger.info("原始请求体长度: {} 字符", rawBody != null ? rawBody.length() : 0);
             logger.info("原始请求体内容: {}", rawBody);

             // 手动解析JSON
             ObjectMapper objectMapper = new ObjectMapper();
             PushData<RecInfo> pushData;
             try {
                 TypeReference<PushData<RecInfo>> typeRef = new TypeReference<PushData<RecInfo>>() {};
                 pushData = objectMapper.readValue(rawBody, typeRef);
                 logger.info("✅ JSON解析成功");
             } catch (Exception parseException) {
                 logger.error("❌ JSON解析失败: {}", parseException.getMessage(), parseException);
                 return ResponseEntity.badRequest().body(new Response(400, "JSON解析失败: " + parseException.getMessage()));
             }

             logger.info("通过Subscribe/Verify接收到认证记录推送，操作类型: {}", pushData.getOperator());
             
             // 1. 校验推送类型（兼容 RecPush 与 VerifyPush）
             String op = pushData.getOperator();
             if (!"RecPush".equals(op) && !"VerifyPush".equals(op)) {
                 logger.warn("无效的认证记录数据，操作类型: {}", pushData.getOperator());
                 return ResponseEntity.badRequest().body(new Response(400, "无效的认证记录数据"));
             }
             
             // 2. 解析认证信息
             RecInfo info = pushData.getInfo();
             if (info == null) {
                 logger.warn("认证记录数据为空");
                 return ResponseEntity.badRequest().body(new Response(400, "认证记录数据为空"));
             }
             
             logger.info("=== 接收到认证记录 ===");
             logger.info("人员ID: {}", info.getCustomId());
             logger.info("认证结果: {} (1=允许，2=拒绝，22=待核验)", info.getVerifyStatus());
             logger.info("相似度: {}%", info.getSimilarity1());
             logger.info("记录ID: {}", info.getRecordID());
             logger.info("设备ID: {}", info.getFacesluiceId());
             
             // 3. 处理认证记录数据
             faceRecognitionService.processRecognitionRecord(info);
             
             // 4. 断点续传：返回确认响应（必须在10秒内）
             Response response = new Response(200, "OK");
             logger.info("✅ 处理成功 - 返回响应: {}", response.toString());
             logger.info("========== /Subscribe/Verify 请求处理完成 ==========\n");
             return ResponseEntity.ok(response);
             
         } catch (Exception e) {
             logger.error("处理认证记录推送失败", e);
             return ResponseEntity.internalServerError().body(new Response(500, "处理失败: " + e.getMessage()));
         }
     }
     
     /**
      * 陌生人抓拍推送接口 - 匹配设备默认配置
      * @param pushData 推送数据
      * @return 响应结果
      */
     @PostMapping("/StrSnapPush")
     public ResponseEntity<Response> strSnapPush(@RequestBody PushData<StrangerInfo> pushData) {
         try {
             logger.info("通过Subscribe/StrSnapPush接收到陌生人抓拍推送，操作类型: {}", pushData.getOperator());
             
             // 1. 校验推送类型
             if (!"StrSnapPush".equals(pushData.getOperator())) {
                 logger.warn("无效的陌生人抓拍数据，操作类型: {}", pushData.getOperator());
                 return ResponseEntity.badRequest().body(new Response(400, "无效的陌生人抓拍数据"));
             }
             
             // 2. 解析陌生人信息
             StrangerInfo info = pushData.getInfo();
             if (info == null) {
                 logger.warn("陌生人抓拍数据为空");
                 return ResponseEntity.badRequest().body(new Response(400, "陌生人抓拍数据为空"));
             }
             
             logger.info("\n=== 接收到陌生人抓拍 ===");
             logger.info("抓拍时间: {}", info.getTime());
             logger.info("抓拍ID: {}", info.getSnapID());
             logger.info("设备ID: {}", info.getFacesluiceId());
             
             // 3. 处理陌生人抓拍数据
             faceRecognitionService.processStrangerRecord(info);
             
             // 4. 断点续传：返回确认响应
             Response response = new Response(200, "OK");
             logger.info("✅ 处理成功 - 返回响应: {}", response.toString());
             logger.info("========== /Subscribe/Snap 请求处理完成 ==========\n");
             
             return ResponseEntity.ok(response);
             
         } catch (Exception e) {
             logger.error("\n❌ 处理陌生人抓拍推送失败");
             logger.error("错误类型: {}", e.getClass().getSimpleName());
             logger.error("错误消息: {}", e.getMessage());
             logger.error("错误堆栈:", e);
             logger.error("========== /Subscribe/Snap 请求处理失败 ==========\n");
             
             return ResponseEntity.internalServerError().body(new Response(500, "处理失败: " + e.getMessage()));
         }
     }


}