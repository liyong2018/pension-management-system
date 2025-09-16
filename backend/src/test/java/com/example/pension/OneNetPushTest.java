package com.example.pension;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OneNetPushTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testOneNetPushWithOfflineStatus() throws NoSuchAlgorithmException {
        String endpoint = "/api/onenet/watch";
        
        // 测试数据 - 离线状态
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"离线\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "4KNbK6m3";
        String token = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
        
        // 计算签名
        String strToVerify = token + nonce + msg;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(md5Bytes);
        
        System.out.println("Token: " + token);
        System.out.println("Nonce: " + nonce);
        System.out.println("Msg: " + msg);
        System.out.println("String to verify: " + strToVerify);
        System.out.println("Calculated signature: " + signature);
        
        // 构建请求体
        String requestBody = String.format("msg=%s&nonce=%s&signature=%s", 
            java.net.URLEncoder.encode(msg, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(nonce, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(signature, StandardCharsets.UTF_8));
        
        // 构建完整URL
        String url = "http://localhost:" + port + endpoint;
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        
        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        
        // 发送HTTP请求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        
        System.out.println("\nResponse Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Headers: " + response.getHeaders());
    }
    
    @Test
    public void testOneNetPushWithOnlineStatus() throws NoSuchAlgorithmException {
        String endpoint = "/api/onenet/watch";
        
        // 测试数据 - 在线状态
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"在线\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "4KNbK6m3";
        String token = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
        
        // 计算签名
        String strToVerify = token + nonce + msg;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(md5Bytes);
        
        System.out.println("\n=== 测试在线状态 ===");
        System.out.println("Token: " + token);
        System.out.println("Nonce: " + nonce);
        System.out.println("Msg: " + msg);
        System.out.println("String to verify: " + strToVerify);
        System.out.println("Calculated signature: " + signature);
        
        // 构建请求体
        String requestBody = String.format("msg=%s&nonce=%s&signature=%s", 
            java.net.URLEncoder.encode(msg, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(nonce, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(signature, StandardCharsets.UTF_8));
        
        // 构建完整URL
        String url = "http://localhost:" + port + endpoint;
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        
        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        
        // 发送HTTP请求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        
        System.out.println("\nResponse Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Headers: " + response.getHeaders());
    }
}