package com.example.pension.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件类型（只允许图片）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只允许上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件大小（最大2MB）
            long maxSize = 2 * 1024 * 1024; // 2MB
            if (file.getSize() > maxSize) {
                response.put("success", false);
                response.put("message", "文件大小不能超过2MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String fullUploadDir = uploadDir + "/" + datePath;
            Path uploadPath = Paths.get(fullUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 构建访问URL
            String fileUrl = "/api/files/" + datePath + "/" + newFilename;
            
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("url", fileUrl);
            response.put("originalName", originalFilename);
            response.put("size", file.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/files/**")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
        try {
            // 从请求URI中提取文件路径
            String requestURI = request.getRequestURI();
            String filePath = requestURI.substring("/api/files/".length());
            
            Path fullPath = Paths.get(uploadDir, filePath);
            
            if (!Files.exists(fullPath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] content = Files.readAllBytes(fullPath);
            String contentType = Files.probeContentType(fullPath);
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(content);
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
} 