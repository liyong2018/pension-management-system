package com.example.pension.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OneNetPushControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${onenet.watch.token}")
    private String token;

    @Test
    public void testVerifyUrl() throws Exception {
        // 测试参数
        String nonce = "randomNonce123";
        String msg = "testMessage456";

        // 计算签名
        String strToVerify = token + nonce + msg;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(md5Bytes);

        // 发送GET请求进行URL验证
        mockMvc.perform(MockMvcRequestBuilders.get("/api/onenet/watch")
                .param("msg", msg)
                .param("nonce", nonce)
                .param("signature", signature))
                .andExpect(status().isOk())
                .andExpect(content().string(msg));
    }

    @Test
    public void testVerifyUrlWithInvalidSignature() throws Exception {
        // 测试参数
        String nonce = "randomNonce123";
        String msg = "testMessage456";
        String invalidSignature = "invalidSignature";

        // 发送GET请求进行URL验证，使用无效签名
        mockMvc.perform(MockMvcRequestBuilders.get("/api/onenet/watch")
                .param("msg", msg)
                .param("nonce", nonce)
                .param("signature", invalidSignature))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHealthCheck() throws Exception {
        // 发送GET请求进行健康检查
        mockMvc.perform(MockMvcRequestBuilders.get("/api/onenet/health"))
                .andExpect(status().isOk());
    }
}