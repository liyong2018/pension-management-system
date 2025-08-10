package com.example.pension.controller;

import com.example.pension.annotation.Loggable;
import com.example.pension.dto.LoginRequest;
import com.example.pension.dto.LoginResponse;
import com.example.pension.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Loggable(module = "认证管理", operationType = "用户登录", description = "用户登录系统")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid username or password"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    // 临时端点：生成密码哈希（仅用于调试）
    @GetMapping("/generate-hash/{password}")
    public ResponseEntity<String> generateHash(@PathVariable String password) {
        String hash = userService.generatePasswordHash(password);
        return ResponseEntity.ok(hash);
    }
    
    // 错误响应类
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}