package com.example.pension.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 测试OneNET平台推送离线状态数据的处理
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OneNetOfflineDataTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "pension-management-system";

    @Test
    public void testOfflineDataPush() throws Exception {
        // 模拟OneNET平台推送的离线状态数据（来自实际日志）
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"离线\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "4KNbK6m3";
        
        // 计算签名
        String signature = calculateSignature(msg, nonce, TOKEN);
        
        System.out.println("测试离线状态数据推送:");
        System.out.println("msg: " + msg);
        System.out.println("nonce: " + nonce);
        System.out.println("signature: " + signature);
        
        // 发送POST请求
        mockMvc.perform(MockMvcRequestBuilders.post("/api/onenet/watch")
                .param("msg", msg)
                .param("nonce", nonce)
                .param("signature", signature))
                .andExpect(status().isOk());
    }
    
    /**
     * 计算OneNET平台的签名
     */
    private String calculateSignature(String msg, String nonce, String token) throws Exception {
        String data = msg + nonce + token;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}