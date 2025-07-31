package org.example.authorizationserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    // 无需认证
    @GetMapping("/health/public")
    public String publicHealthCheck() {
        return "Public Health: 服务运行正常！";
    }

    // 需要登录认证
    @GetMapping("/health/private")
    public String privateHealthCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Private Health: 认证用户 [" + authentication.getName() + "] 访问成功！";
    }


}