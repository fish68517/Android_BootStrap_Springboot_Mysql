package com.graduation.entity;

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
        // 这将解析为 "src/main/resources/templates/admin/login.html"
        return "admin/login";
    }

    /**
     * 处理管理员登录表单提交
     * (注意：这里只是一个简单的演示，没有集成 Spring Security)
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password) {

        System.out.println("登录尝试: " + username);
        // 伪代码：实际项目中您需要在这里调用 Service 验证用户名和密码
        // if (usersService.validateAdmin(username, password)) { ... }

        // 假设登录成功，重定向到管理员主页
        return "redirect:/admin/dashboard";
    }

    /**
     * 显示管理员主页（登录成功后）
     * @return 模板路径 "admin/dashboard"
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        // 这将解析为 "src/main/resources/templates/admin/dashboard.html"
        return "admin/dashboard";
    }
}