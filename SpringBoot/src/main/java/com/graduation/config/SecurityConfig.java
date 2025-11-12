package com.graduation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
}
