package com.example.pension.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 测试OneNET离线状态数据处理
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OneNetOfflineStatusTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    private int port;
    
    private final String token = "testToken123";
    
    @Test
    public void testOfflineStatusProcessing() throws Exception {
        // 模拟OneNET推送的离线状态数据
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"离线\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "4KNbK6m3";
        
        // 计算签名
        String signature = calculateSignature(msg, nonce);
        
        System.out.println("测试数据:");
        System.out.println("msg: " + msg);
        System.out.println("nonce: " + nonce);
        System.out.println("signature: " + signature);
        
        // 准备请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("msg", msg);
        params.add("nonce", nonce);
        params.add("signature", signature);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        // 发送POST请求
        String url = "http://localhost:" + port + "/api/onenet/watch";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            System.out.println("响应状态: " + response.getStatusCode());
            System.out.println("响应内容: " + response.getBody());
            System.out.println("响应头: " + response.getHeaders());
            
            // 验证响应
            if (response.getStatusCode() != HttpStatus.OK) {
                System.err.println("期望状态码: 200, 实际状态码: " + response.getStatusCode().value());
            }
            if (!"数据接收成功".equals(response.getBody())) {
                System.err.println("期望响应内容: 数据接收成功, 实际响应内容: " + response.getBody());
            }
            
            assert response.getStatusCode() == HttpStatus.OK : "状态码不匹配: " + response.getStatusCode();
            assert "数据接收成功".equals(response.getBody()) : "响应内容不匹配: " + response.getBody();
        } catch (Exception e) {
            System.err.println("请求异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testOnlineStatusProcessing() throws Exception {
        // 测试在线状态
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"佩戴\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "testNonce2";
        
        String signature = calculateSignature(msg, nonce);
        
        // 准备请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("msg", msg);
        params.add("nonce", nonce);
        params.add("signature", signature);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        // 发送POST请求
        String url = "http://localhost:" + port + "/api/onenet/watch";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            System.out.println("在线状态测试 - 响应状态: " + response.getStatusCode());
            System.out.println("在线状态测试 - 响应内容: " + response.getBody());
            
            // 验证响应
            if (response.getStatusCode() != HttpStatus.OK) {
                System.err.println("在线状态测试 - 期望状态码: 200, 实际状态码: " + response.getStatusCode().value());
            }
            if (!"数据接收成功".equals(response.getBody())) {
                System.err.println("在线状态测试 - 期望响应内容: 数据接收成功, 实际响应内容: " + response.getBody());
            }
            
            assert response.getStatusCode() == HttpStatus.OK : "状态码不匹配: " + response.getStatusCode();
            assert "数据接收成功".equals(response.getBody()) : "响应内容不匹配: " + response.getBody();
        } catch (Exception e) {
            System.err.println("在线状态测试请求异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 计算OneNET签名
     */
    private String calculateSignature(String msg, String nonce) throws Exception {
        String strToVerify = token + nonce + msg;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(md5Bytes);
    }
}