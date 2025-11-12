package com.graduation.util;

import com.graduation.entity.Users;
import jakarta.servlet.http.HttpSession;

/**
 * Session工具类
 * 提供便捷的方法从session获取当前用户信息
 */
public class SessionUtil {

    /**
     * 从session获取当前登录用户
     * 
     * @param session HttpSession对象
     * @return 当前登录的用户对象，如果未登录返回null
     */
    public static Users getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Users) session.getAttribute("currentUser");
    }

    /**
     * 从session获取当前用户ID
     * 
     * @param session HttpSession对象
     * @return 当前用户ID，如果未登录返回null
     */
    public static Integer getCurrentUserId(HttpSession session) {
        Users user = getCurrentUser(session);
        return user != null ? user.getUserId() : null;
    }

    /**
     * 从session获取当前用户角色
     * 
     * @param session HttpSession对象
     * @return 当前用户角色，如果未登录返回null
     */
    public static String getCurrentUserRole(HttpSession session) {
        Users user = getCurrentUser(session);
        return user != null ? user.getRole() : null;
    }

    /**
     * 检查用户是否已登录
     * 
     * @param session HttpSession对象
     * @return 如果已登录返回true，否则返回false
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    /**
     * 检查当前用户是否为管理员
     * 
     * @param session HttpSession对象
     * @return 如果是管理员返回true，否则返回false
     */
    public static boolean isAdmin(HttpSession session) {
        String role = getCurrentUserRole(session);
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * 检查当前用户是否为发布者或管理员
     * 
     * @param session HttpSession对象
     * @return 如果是发布者或管理员返回true，否则返回false
     */
    public static boolean isPublisherOrAdmin(HttpSession session) {
        String role = getCurrentUserRole(session);
        return "publisher".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
    }
}
