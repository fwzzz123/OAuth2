package org.example.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 配置安全过滤链（替代旧版的 and() 链式调用）
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置登录页（新版写法，移除 and() 链式调用）
                .formLogin(form -> form
                        .loginPage("/login") // 显式指定登录页路径
                        .permitAll()        // 允许匿名访问登录页
                )
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll() // 健康检查接口允许匿名访问
                        .anyRequest().authenticated()           // 其他请求需要认证
                );

        return http.build();
    }

    // 内存用户（测试用）
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}password") // {noop} 表示不加密（仅测试用）
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}