package org.example.authorizationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 标记为 Spring Boot 应用，开启自动配置、包扫描
@SpringBootApplication
public class AuthorizationServerApplication {
    public static void main(String[] args) {
        // 启动 Spring Boot
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}