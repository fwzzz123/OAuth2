// src/main/java/org/example/authorizationserver/AuthorizationServerApplication.java
package org.example.authorizationserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OAuth2 授权服务器启动类
 */
@SpringBootApplication
@MapperScan("org.example.authorizationserver.mapper")
public class AuthorizationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}