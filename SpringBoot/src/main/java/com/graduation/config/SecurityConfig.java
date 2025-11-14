package com.graduation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // 确保导入
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Spring Security配置类
 * 配置URL访问权限、登录登出、CSRF等安全设置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /**
     * 配置安全过滤链
     */
    @Bean
    @Order(2) // 优先级设为2，匹配所有其他请求
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置URL访问权限
            .authorizeHttpRequests(auth -> auth
                // 公开访问的URL（无需登录）
                .requestMatchers(
                    "/user/register",
                    "/user/login",
                    "/game/list",
                    "/game/detail/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/vendor/**",
                    "/",
                    "/error"
                ).permitAll()

                // 管理员专用URL
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 发布者和管理员可访问的URL
                .requestMatchers(
                    "/game/publish",
                    "/game/edit/**",
                    "/game/my-games"
                ).hasAnyRole("PUBLISHER", "ADMIN")

                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )

            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            )

            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 禁用CSRF（简化开发，生产环境需启用）
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * 密码编码器Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }


    // ==================================================================
    // 【【【修改点 1：为 Android API 单独配置安全链】】】
    // ==================================================================
    @Bean
    @Order(1) // 优先级设为1，首先匹配
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 匹配所有 /api/ 开头的路径
                .securityMatcher("/api/**")

                // 禁用 CSRF 防护（API通常使用 Token 或 Session Cookie，不需要 CSRF）
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize
                        // 明确允许 API 的登录和注册接口
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                        // 其他所有 /api/** 路径也不需要身份认证
                        .requestMatchers("/api/**").permitAll()
                        // .anyRequest().authenticated()
                )


                // 【关键】: 配置异常处理
                .exceptionHandling(exceptions -> exceptions
                        // 告诉 Spring Security 返回 401 (Unauthorized) 状态码，而不是重定向
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // API 可以重用 Web 的会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}
