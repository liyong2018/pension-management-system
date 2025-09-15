package com.example.pension.controller;

import com.example.pension.model.onenet.OneNetPushData;
import com.example.pension.model.onenet.WatchData;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OneNetPushDataTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "pension-management-system";

    @Test
    public void testReceiveData() throws Exception {
        // 创建测试数据
        OneNetPushData<WatchData> pushData = new OneNetPushData<>();
        pushData.setMsgId("test-msg-id-001");
        pushData.setDeviceId("test-device-001");
        pushData.setTimestamp(System.currentTimeMillis());
        pushData.setMsgType("data");

        WatchData watchData = new WatchData();
        watchData.setHeartRate(75);
        watchData.setSystolicPressure(120);
        watchData.setDiastolicPressure(80);
        watchData.setBloodOxygen(98);
        watchData.setBodyTemperature(36.5f);
        watchData.setSteps(5000);
        watchData.setSleepStatus(0);
        watchData.setLongitude(116.3);
        watchData.setLatitude(39.9);
        watchData.setBatteryLevel(85);
        watchData.setCharging(false);
        watchData.setDeviceStatus("normal");

        pushData.setData(watchData);

        // 将数据转换为JSON字符串
        String msg = objectMapper.writeValueAsString(pushData);
        String nonce = "randomNonce456";

        // 计算签名
        String strToVerify = TOKEN + nonce + msg;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(md5Bytes);

        // 发送POST请求接收数据
        mockMvc.perform(MockMvcRequestBuilders.post("/api/onenet/watch")
                .param("msg", msg)
                .param("nonce", nonce)
                .param("signature", signature))
                .andExpect(status().isOk())
                .andExpect(content().string("数据接收成功"));
    }

    @Test
    public void testReceiveDataWithInvalidSignature() throws Exception {
        // 创建测试数据
        OneNetPushData<WatchData> pushData = new OneNetPushData<>();
        pushData.setMsgId("test-msg-id-002");
        pushData.setDeviceId("test-device-002");
        pushData.setTimestamp(System.currentTimeMillis());
        pushData.setMsgType("data");

        WatchData watchData = new WatchData();
        watchData.setHeartRate(80);
        pushData.setData(watchData);

        // 将数据转换为JSON字符串
        String msg = objectMapper.writeValueAsString(pushData);
        String nonce = "randomNonce789";
        String invalidSignature = "invalidSignature";

        // 发送POST请求接收数据，使用无效签名
        mockMvc.perform(MockMvcRequestBuilders.post("/api/onenet/watch")
                .param("msg", msg)
                .param("nonce", nonce)
                .param("signature", invalidSignature))
                .andExpect(status().isBadRequest());
    }
}