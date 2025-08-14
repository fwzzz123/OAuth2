package org.example.authorizationserver.controller;
import org.example.authorizationserver.entity.Users;
import org.example.authorizationserver.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 提供用户信息的API Controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUsersService usersService;

    /**
     * 获取当前登录用户的详细信息
     * Vue前端可以在登录成功后调用此接口来更新UI
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 检查用户是否已认证
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }
        String username = authentication.getName();
        Users user = usersService.findByUsername(username);
        // 隐藏密码等敏感信息
        if (user != null) {
            user.setPassword(null);
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 获取所有用户列表（仅限ADMIN访问）
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersService.list();
        // 隐藏所有用户的密码
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    // 您可以在此处添加更多用于管理用户的API，例如：
    // @PostMapping, @PutMapping("/{id}"), @DeleteMapping("/{id}")
}