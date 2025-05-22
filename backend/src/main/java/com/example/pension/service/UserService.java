package com.example.pension.service;

import com.example.pension.dao.UserDao;
import com.example.pension.dto.LoginRequest;
import com.example.pension.dto.LoginResponse;
import com.example.pension.dto.UserDTO;
import com.example.pension.model.User;
import com.example.pension.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userDao.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userDao.update(user);

        // 生成JWT token
        String token = tokenProvider.generateToken(user.getUsername());

        // 重新获取用户信息，以确保 DTO 中是最新的，特别是 updateTime
        User updatedUser = userDao.findById(user.getId());
        return new LoginResponse(token, convertToDTO(updatedUser != null ? updatedUser : user));
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        if (user.getOrganization() != null) {
            dto.setOrganizationId(user.getOrganization().getId());
            if (user.getOrganization().getName() != null) {
                dto.setOrganizationName(user.getOrganization().getName());
            }
        }
        dto.setAdmin(user.isAdmin());
        dto.setActive(user.isActive());
        dto.setLastLoginTime(user.getLastLoginTime());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }
} 