package com.example.pension.controller;

import com.example.pension.model.onenet.OneNetPushData;
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
     * @param msg 推送的消息内容，JSON格式的设备数据
     * @param nonce 平台生成的随机字符串
     * @param signature 加密签名
     * @return 处理结果
     */
    @PostMapping("/watch")
    public ResponseEntity<String> receiveData(
            @RequestParam("msg") String msg,
            @RequestParam("nonce") String nonce,
            @RequestParam("signature") String signature) {
        
        logger.info("收到OneNET平台数据推送");
        logger.info("nonce: {}", nonce);
        logger.info("signature: {}", signature);
        
        try {
            // 1. 验证签名
            String strToVerify = token + nonce + msg;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(md5Bytes);
            
            if (!signature.equals(calculatedSignature)) {
                logger.warn("OneNET平台数据推送验证失败，签名不匹配");
                return ResponseEntity.badRequest().body("签名验证失败");
            }
            
            // 2. 解析数据
            ObjectMapper objectMapper = new ObjectMapper();
            OneNetPushData<WatchData> pushData = objectMapper.readValue(msg, 
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
            
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5算法不可用", e);
            return ResponseEntity.internalServerError().body("服务器内部错误");
        } catch (Exception e) {
            logger.error("数据处理过程发生异常", e);
            return ResponseEntity.internalServerError().body("服务器内部错误: " + e.getMessage());
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