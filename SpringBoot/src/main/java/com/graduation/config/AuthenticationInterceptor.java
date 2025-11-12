package com.graduation.config;

import com.graduation.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 验证用户session，确保用户已登录
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // 检查session是否存在以及是否包含用户信息
        if (session == null || session.getAttribute("currentUser") == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        
        // 已登录，允许继续
        return true;
    }
}
