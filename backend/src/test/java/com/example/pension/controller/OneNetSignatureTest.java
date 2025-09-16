package com.example.pension.controller;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OneNetSignatureTest {
    
    private static final String TOKEN = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
    
    @Test
    public void testSignatureCalculation() throws NoSuchAlgorithmException {
        // 测试数据
        String msg = "{\"notifyType\":\"property\",\"productId\":\"n7Va2Dm5HY\",\"messageType\":\"notify\",\"data\":{\"id\":\"0\",\"params\":{\"imie\":{\"time\":1757948604527,\"value\":\"860870050503148\"},\"Wear\":{\"time\":1757948604533,\"value\":\"离线\"}}},\"deviceName\":\"860870050503148\"}";
        String nonce = "4KNbK6m3";
        String expectedSignature = "2s+sqRL8FG8KJ1pjFjC0WA==";
        
        System.out.println("Token: " + TOKEN);
        System.out.println("Nonce: " + nonce);
        System.out.println("Msg: " + msg);
        System.out.println("Expected signature: " + expectedSignature);
        
        // 计算签名
        String strToVerify = TOKEN + nonce + msg;
        System.out.println("String to verify: " + strToVerify);
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(strToVerify.getBytes(StandardCharsets.UTF_8));
        String calculatedSignature = Base64.getEncoder().encodeToString(md5Bytes);
        
        System.out.println("Calculated signature: " + calculatedSignature);
        System.out.println("Signatures match: " + expectedSignature.equals(calculatedSignature));
        
        // 打印MD5字节数组
        System.out.print("MD5 bytes: ");
        for (byte b : md5Bytes) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }
}