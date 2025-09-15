package com.example.pension.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * OneNET平台推送接收演示控制器
 * 用于快速测试OneNET平台推送功能
 */
@RestController
@RequestMapping("/api/demo/onenet")
@CrossOrigin(origins = "*")
public class OneNetPushDemoController {

    private static final Logger logger = LoggerFactory.getLogger(OneNetPushDemoController.class);
    
    // OneNET平台配置的token，用于验证请求来源，32位数字或英文字母的组合
    private static final String TOKEN = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
    
    /**
     * URL验证接口
     * OneNET平台会向此URL发送HTTP GET请求进行URL验证
     * 
     * @param msg 推送的消息内容，实例验证阶段为平台生成的随机字符串
     * @param nonce 平台生成的随机字符串
     * @param signature 加密签名
     * @return 验证成功返回msg参数值，验证失败返回错误信息
     */
    @GetMapping("/push")
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
            String strToVerify = TOKEN + nonce + msg;
            
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
    @PostMapping("/push")
    public ResponseEntity<String> receiveData(
            @RequestParam("msg") String msg,
            @RequestParam("nonce") String nonce,
            @RequestParam("signature") String signature) {
        
        logger.info("收到OneNET平台数据推送");
        logger.info("nonce: {}", nonce);
        logger.info("signature: {}", signature);
        
        try {
            // 1. 验证签名
            String strToVerify = TOKEN + nonce + msg;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(md5Bytes);
            
            if (!signature.equals(calculatedSignature)) {
                logger.warn("OneNET平台数据推送验证失败，签名不匹配");
                return ResponseEntity.badRequest().body("签名验证失败");
            }
            
            // 2. 打印接收到的数据
            logger.info("接收到的数据: {}", msg);
            System.out.println("============= OneNET推送数据 ==============");
            System.out.println(msg);
            System.out.println("============================================");
            
            return ResponseEntity.ok("数据接收成功");
            
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
    public String health() {
        return "OneNET Push Demo Service is running!";
    }
}