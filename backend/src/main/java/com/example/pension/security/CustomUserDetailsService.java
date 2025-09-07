package com.example.pension.security;

import com.example.pension.dao.UserDao;
import com.example.pension.dao.MenuPermissionDao;
import com.example.pension.model.User;
import com.example.pension.model.MenuPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // 手动添加日志变量
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserDao userDao;
    private final MenuPermissionDao menuPermissionDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加基本角色
        authorities.add(new SimpleGrantedAuthority(user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));
        
        // 尝试获取用户的所有权限
        try {
            List<MenuPermission> permissions = menuPermissionDao.findByUserId(user.getId());
            // 添加具体权限
            if (permissions != null) {
                authorities.addAll(permissions.stream()
                    .filter(permission -> permission.getPermissionKey() != null && !permission.getPermissionKey().trim().isEmpty())
                    .map(permission -> new SimpleGrantedAuthority(permission.getPermissionKey()))
                    .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            // 如果权限查询失败，记录日志但不影响基本角色权限
            log.warn("Failed to load permissions for user: {}, error: {}", username, e.getMessage());
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(!user.isActive())
                .accountLocked(!user.isActive())
                .credentialsExpired(!user.isActive())
                .disabled(!user.isActive())
                .build();
    }
}