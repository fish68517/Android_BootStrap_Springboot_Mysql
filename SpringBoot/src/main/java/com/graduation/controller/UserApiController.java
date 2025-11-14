package com.graduation.controller;

import com.graduation.dto.ApiResponse;
import com.graduation.dto.UserLoginDTO;
import com.graduation.dto.UserRegisterDTO;
import com.graduation.entity.Preferences;
import com.graduation.entity.Users;
import com.graduation.service.PreferenceService;
import com.graduation.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user") // 使用 /api 前缀区分
public class UserApiController {

    @Autowired
    private UserService userService; //

    @Autowired
    private PreferenceService preferenceService; //

    /**
     * 用户登录 (API)
     *
     */
    @PostMapping("/login")
    public ResponseEntity<Users> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpSession session) {
        // userService.login 会在失败时抛出异常，由 ApiExceptionHandler 捕获
        Users user = userService.login(userLoginDTO);
        session.setAttribute("currentUser", user); // 关键：为后续请求建立会话
        return ResponseEntity.ok(user);
    }

    /**
     * 用户注册 (API)
     *
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        // 检查两次密码是否一致
     /*   if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("两次输入的密码不一致"));
        }*/
        // userService.register 会在用户名重复时抛出异常
        userService.register(userRegisterDTO);
        return ResponseEntity.ok(ApiResponse.success("注册成功"));
    }

    /**
     * 获取当前登录的用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<Users> getProfile(HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
    /*    if (currentUser == null) {
            // 抛出异常，让 Handler 处理
            throw new UnauthorizedException("用户未登录");
        }*/
        return ResponseEntity.ok(currentUser);
    }

    /**
     * 获取所有偏好列表 (API)
     *
     */
    @GetMapping("/preferences")
    public ResponseEntity<List<Preferences>> getAllPreferences() {
        List<Preferences> preferences = preferenceService.getAllPreferences();
        return ResponseEntity.ok(preferences);
    }
}