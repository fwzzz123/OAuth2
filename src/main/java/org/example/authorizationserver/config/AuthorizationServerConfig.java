// src/main/java/org/example/authorizationserver/config/AuthorizationServerConfig.java
package org.example.authorizationserver.config;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OAuth2 授权服务器配置
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {


    /**
     * 【新增】
     * 注册一个 ForwardedHeaderFilter Bean。
     * 这个过滤器会解析 Nginx 发送过来的 X-Forwarded-* 请求头，
     * 并将这些信息更新到 HttpServletRequest 对象中。
     * 这是让 Spring 在反向代理后能够正确生成绝对路径 URL (包含正确的协议、主机和端口) 的最可靠方法。
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
    /**
     * 【最终解决方案 - 链 1】
     * 此过滤器链仅负责处理 OAuth2 相关的端点。
     * 它的优先级更高 (@Order(1))。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        // 1. 应用 Spring Authorization Server 的默认安全配置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // 启用 OpenID Connect 1.0

        http
                // 2. 当 OAuth2 相关端点需要认证时，重定向到 /login
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login")
                        )
                )
                // 3. 配置资源服务器（API）接受JWT令牌进行认证
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 【最终解决方案 - 链 2】
     * 此过滤器链负责处理所有其他请求，并提供默认的登录页面。
     * 它的优先级较低 (@Order(2))。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                // 1. 保护所有请求，要求用户必须被认证
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                // 2. 【重要】启用表单登录。这个配置会自动创建 /login 端点并提供一个默认的登录页面。
                //    它也能正确处理登录成功后跳转回原始请求页面的逻辑。
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

//    @Bean
//    @Order(1)
//    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
//            throws Exception {
//        // 1. 应用 Spring Authorization Server 的默认配置
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
//                .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
//
//        http
//                // 2. 配置异常处理，对于未认证的请求，重定向到登录页
//                .exceptionHandling((exceptions) -> exceptions
//                        .defaultAuthenticationEntryPointFor(
//                                //开发环境
////                                new LoginUrlAuthenticationEntryPoint("/login"),
//                                // 生产环境
//                                new LoginUrlAuthenticationEntryPoint("/auth/login"),
//                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                        )
//                )
//                // 3. 配置资源服务器（API）的JWT验证
//                .oauth2ResourceServer((resourceServer) -> resourceServer
//                        .jwt(Customizer.withDefaults()));
//        // 4. 【新增】将 formLogin 和其他所有请求的授权规则也加入到这个链中
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        // 允许所有人访问登录页面，防止重定向循环
//                        //开发环境
////                        .requestMatchers("/login").permitAll()
//                        //生产环境
//                        .requestMatchers("/auth/login").permitAll()
//                        // 其他所有请求都需要认证
//                        .anyRequest().authenticated()
//                )
//                // 使用默认的表单登录
//                .formLogin(Customizer.withDefaults());
//
//        return http.build();
//    }

//    @Bean
//    @Order(2)
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
//            throws Exception {
//        http
//                .authorizeHttpRequests((authorize) -> authorize
//                        .anyRequest().authenticated()
//                )
//                .formLogin(Customizer.withDefaults());
//
//        return http.build();
//    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://localhost:8443/login/oauth2/code/oidc-client")
                // 新增 Postman 的回调地址
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("https://frp-dog.com:26390/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
//        开发环境
          return AuthorizationServerSettings.builder().build();

//        部署环境
//        return AuthorizationServerSettings.builder()
//                .issuer("https://frp-dog.com:26390/")
//                .build();
    }

    /**
     * 【新增配置】
     * 自定义 JWT，将用户的权限（角色）添加到 Token 中。
     * 当客户端（资源服务器）收到此 JWT 后，就可以解析出这些权限信息。
     * @return OAuth2TokenCustomizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            // 检查上下文类型是否为 JWT
            if (context.getTokenType().getValue().equals("access_token")) {
                Authentication principal = context.getPrincipal();
                // 从认证主体中获取权限
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                // 将权限集合添加到 JWT 的 "authorities" claim 中
                context.getClaims().claim("authorities", authorities);
            }
        };
    }

}