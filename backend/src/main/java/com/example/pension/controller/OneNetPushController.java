package com.example.pension.controller;

import com.example.pension.model.onenet.OneNetPushData;
import com.example.pension.model.onenet.OneNetPropertyNotification;
import com.example.pension.model.onenet.OneNetPushRequest;
import com.example.pension.model.onenet.WatchData;
import com.example.pension.service.WatchDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * OneNET平台推送接收控制器
 * 用于接收OneNET平台推送的手环数据
 */
@RestController
@RequestMapping("/api/onenet")
@CrossOrigin(origins = "*")
public class OneNetPushController {

    private static final Logger logger = LoggerFactory.getLogger(OneNetPushController.class);
    
    // OneNET平台配置的token，用于验证请求来源，从配置文件中读取
    @Value("${onenet.watch.token}")
    private String token;
    
    @Autowired
    private WatchDataService watchDataService;
    
    /**
     * URL验证接口
     * OneNET平台会向此URL发送HTTP GET请求进行URL验证
     * 
     * @param msg 推送的消息内容，实例验证阶段为平台生成的随机字符串
     * @param nonce 平台生成的随机字符串
     * @param signature 加密签名
     * @return 验证成功返回msg参数值，验证失败返回错误信息
     */
    @GetMapping("/watch")
    public ResponseEntity<String> verifyUrl(
            @RequestParam("msg") String msg,
            @RequestParam("nonce") String nonce,
            @RequestParam("signature") String signature) {
        
        logger.info("收到OneNET平台URL验证请求");
        logger.info("msg: {}", msg);
        logger.info("nonce: {}", nonce);
        logger.info("signature: {}", signature);
        
        try {
            // 1. 拼接token、nonce和msg
            String strToVerify = token + nonce + msg;
            
            // 2. 进行MD5加密
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
            
            // 3. 进行Base64编码
            String calculatedSignature = Base64.getEncoder().encodeToString(md5Bytes);
            
            // 4. 比较签名
            if (signature.equals(calculatedSignature)) {
                logger.info("OneNET平台URL验证成功");
                // 验证成功，返回msg参数
                return ResponseEntity.ok(msg);
            } else {
                logger.warn("OneNET平台URL验证失败，签名不匹配");
                logger.warn("计算得到的签名: {}", calculatedSignature);
                logger.warn("收到的签名: {}", signature);
                return ResponseEntity.badRequest().body("签名验证失败");
            }
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5算法不可用", e);
            return ResponseEntity.internalServerError().body("服务器内部错误");
        } catch (Exception e) {
            logger.error("URL验证过程发生异常", e);
            return ResponseEntity.internalServerError().body("服务器内部错误");
        }
    }
    
    /**
     * 接收OneNET平台推送的数据
     * 
     * @param request OneNET推送请求对象，包含msg、nonce、signature、time、id等字段
     * @return 处理结果
     */
    @PostMapping("/watch")
    public ResponseEntity<String> receiveData(@RequestBody OneNetPushRequest request) {
        
        logger.info("收到OneNET平台数据推送");
        logger.info("nonce: {}", request.getNonce());
        logger.info("signature: {}", request.getSignature());
        logger.info("time: {}", request.getTime());
        logger.info("id: {}", request.getId());
        
        try {
            // 1. 验证签名
            if (!verifySignature(request.getMsg(), request.getNonce(), request.getSignature())) {
                logger.warn("签名验证失败");
                return ResponseEntity.badRequest().body("签名验证失败");
            }
            
            // 2. 解析数据 - 先尝试解析为属性通知格式
            ObjectMapper objectMapper = new ObjectMapper();
            
            try {
                // 尝试解析为OneNET属性通知格式
                OneNetPropertyNotification notification = objectMapper.readValue(request.getMsg(), OneNetPropertyNotification.class);
                logger.info("接收到OneNET属性通知: 设备={}, 类型={}", notification.getDeviceName(), notification.getNotifyType());
                
                // 处理属性通知数据
                boolean success = processPropertyNotification(notification);
                
                if (success) {
                    return ResponseEntity.ok("数据接收成功");
                } else {
                    return ResponseEntity.internalServerError().body("数据处理失败");
                }
                
            } catch (Exception e1) {
                logger.info("尝试解析为标准手环数据格式");
                
                try {
                    // 如果属性通知格式解析失败，尝试原有的手环数据格式
                    OneNetPushData<WatchData> pushData = objectMapper.readValue(request.getMsg(), 
                            objectMapper.getTypeFactory().constructParametricType(OneNetPushData.class, WatchData.class));
                    
                    // 3. 处理数据
                    WatchData watchData = pushData.getData();
                    String deviceId = pushData.getDeviceId();
                    Long timestamp = pushData.getTimestamp();
                    logger.info("接收到手环数据: 设备ID={}, 时间戳={}, 数据={}", deviceId, timestamp, watchData);
                    
                    // 调用服务处理数据
                    boolean success = watchDataService.processWatchData(deviceId, watchData, timestamp);
                    
                    if (success) {
                        return ResponseEntity.ok("数据接收成功");
                    } else {
                        return ResponseEntity.internalServerError().body("数据处理失败");
                    }
                } catch (Exception e2) {
                    logger.error("数据解析失败，无法识别的数据格式", e2);
                    return ResponseEntity.badRequest().body("数据格式不支持");
                }
            }
            
            
        } catch (Exception e) {
            logger.error("数据处理过程发生异常", e);
            return ResponseEntity.internalServerError().body("服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 验证OneNET平台推送数据的签名
     */
    private boolean verifySignature(String msg, String nonce, String signature) {
        try {
            logger.info("开始验证签名");
            logger.info("Token: {}", token);
            logger.info("Nonce: {}", nonce);
            logger.info("Msg: {}", msg);
            logger.info("Received signature: {}", signature);
            
            String strToVerify = token + nonce + msg;
            logger.info("String to verify: {}", strToVerify);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(md5Bytes);
            
            logger.info("Calculated signature: {}", calculatedSignature);
            logger.info("Signatures match: {}", signature.equals(calculatedSignature));
            
            return signature.equals(calculatedSignature);
        } catch (Exception e) {
            logger.error("签名验证过程中发生异常", e);
            return false;
        }
    }
    
    /**
     * 处理OneNET属性通知数据
     */
    private boolean processPropertyNotification(OneNetPropertyNotification notification) {
        try {
            String deviceName = notification.getDeviceName();
            logger.info("处理设备 {} 的属性通知", deviceName);
            
            if (notification.getData() != null && notification.getData().getParams() != null) {
                var params = notification.getData().getParams();
                
                // 处理IMEI信息
                if (params.containsKey("imie")) {
                    var imieValue = params.get("imie");
                    logger.info("设备IMEI: {}, 时间: {}", imieValue.getStringValue(), imieValue.getTime());
                }
                
                // 处理佩戴状态（包括离线状态）
                if (params.containsKey("Wear")) {
                    var wearValue = params.get("Wear");
                    String wearStatus = wearValue.getStringValue();
                    logger.info("设备佩戴状态: {}, 时间: {}", wearStatus, wearValue.getTime());
                    
                    // 创建WatchData对象来存储状态信息
                    WatchData watchData = new WatchData();
                    watchData.setDeviceStatus(wearStatus);
                    
                    // 调用服务处理数据
                    return watchDataService.processWatchData(deviceName, watchData, wearValue.getTime());
                }
                
                // 处理其他可能的参数
                for (var entry : params.entrySet()) {
                    logger.info("参数 {}: 值={}, 时间={}", 
                        entry.getKey(), 
                        entry.getValue().getStringValue(), 
                        entry.getValue().getTime());
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.error("处理属性通知数据时发生异常", e);
            return false;
        }
    }
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "OneNET Push Service");
        return ResponseEntity.ok(response);
    }
}