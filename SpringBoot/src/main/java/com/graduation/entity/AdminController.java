package com.graduation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin") // 所有管理员相关的 URL 都在 /admin 路径下
public class AdminController {

    /**
     * 显示管理员登录页面
     * @return 模板路径 "admin/login"
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin/login";
    }

    /**
     * 处理管理员登录表单提交
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password) {

        System.out.println("登录尝试: " + username);
        // 伪代码：实际项目中您需要在这里调用 Service 验证用户名和密码

        // 假设登录成功，重定向到管理员主页
        return "redirect:/admin/dashboard";
    }

    /**
     * 1. 显示管理员主页（登录成功后）
     * @return 模板路径 "admin/dashboard"
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin/dashboard";
    }

    /**
     * 2. 【新功能】显示排行榜页面
     * @return 模板路径 "admin/leaderboard"
     */
    @GetMapping("/leaderboard")
    public String showLeaderboard() {
        return "admin/leaderboard";
    }

    /**
     * 3. 【新功能】显示收藏页面
     * @return 模板路径 "admin/favorites"
     */
    @GetMapping("/favorites")
    public String showFavorites() {
        return "admin/favorites";
    }

    /**
     * 4. 【新功能】显示点赞页面
     * @return 模板路径 "admin/likes"
     */
    @GetMapping("/likes")
    public String showLikes() {
        return "admin/likes";
    }

    /**
     * 5. 【新功能】显示个人中心页面
     * @return 模板路径 "admin/profile"
     */
    @GetMapping("/profile")
    public String showProfile() {
        return "admin/profile";
    }
}