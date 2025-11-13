package com.graduation.controller;

import com.graduation.dto.UserLoginDTO;
import com.graduation.dto.UserRegisterDTO;
import com.graduation.entity.Preferences;
import com.graduation.entity.Users;
import com.graduation.exception.DuplicateUsernameException;
import com.graduation.exception.UnauthorizedException;
import com.graduation.exception.UserNotFoundException;
import com.graduation.service.PreferenceService;
import com.graduation.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 用户控制器
 * 处理用户注册、登录、资料管理等请求
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PreferenceService preferenceService;

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "user/register";
    }

    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDTO dto, 
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        // 验证表单数据
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            // 注册用户
            userService.register(dto);
            redirectAttributes.addFlashAttribute("successMessage", "注册成功！请登录");
            return "redirect:/user/login";
        } catch (DuplicateUsernameException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "注册失败，请稍后重试");
            return "user/register";
        }
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        // 如果已登录，重定向到首页
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser != null) {
            return "redirect:/";
        }
        
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "user/login";
    }

    /**
     * 处理用户登录
     * 注意：实际的认证由Spring Security 处理
     * 这个方法主要用于显示登录表单和处理登录错误
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserLoginDTO dto,
                       BindingResult bindingResult,
                       HttpSession session,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        // 验证表单数据
        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        System.out.println(" login 用户名：" + dto.getUsername());
        System.out.println("密码：" + dto.getPassword());
        System.out.println("角色：" + dto.getRole());

        try {
            // 验证登录（用于自定义验证逻辑）
            Users user = userService.login(dto);
            
            // Session由CustomAuthenticationSuccessHandler创建
            // 这里保留代码以支持非Spring Security的登录流程
            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            
            redirectAttributes.addFlashAttribute("successMessage", "登录成功！");
            
            // 检查是否为首次登录（没有设置偏好）
            if (!"admin".equals(user.getRole())) {
                java.util.List<Preferences> userPreferences = preferenceService.getUserPreferences(user.getUserId());
                if (userPreferences == null || userPreferences.isEmpty()) {
                    // 首次登录，提示设置偏好
                    redirectAttributes.addFlashAttribute("showPreferencePrompt", true);
                    return "redirect:/user/preferences";
                }
            }
            
            // 根据角色跳转到不同页面
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        } catch (UserNotFoundException | UnauthorizedException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "登录失败，请稍后重试");
            return "user/login";
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // 清除session
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "已成功退出登录");
        return "redirect:/user/login";
    }

    /**
     * 显示个人资料页面
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        // 获取最新的用户信息
        Users user = userService.getUserById(currentUser.getUserId());
        model.addAttribute("user", user);
        return "user/profile";
    }

    /**
     * 更新个人资料
     */
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String nickname,
                               @RequestParam(required = false) String password,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        try {
            // 构建更新对象
            Users updatedUser = new Users();
            updatedUser.setNickname(nickname);
            if (password != null && !password.trim().isEmpty()) {
                updatedUser.setPasswordHash(password);
            }

            // 更新用户信息
            userService.updateProfile(currentUser.getUserId(), updatedUser);
            
            // 更新session中的用户信息
            Users refreshedUser = userService.getUserById(currentUser.getUserId());
            session.setAttribute("currentUser", refreshedUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "资料更新成功！");
            if ("admin".equals(currentUser.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/user/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "更新失败：" + e.getMessage());
            model.addAttribute("user", currentUser);
            return "user/profile";
        }
    }

    /**
     * 删除账号
     */
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        try {
            // 删除用户
            userService.deleteUser(currentUser.getUserId());
            
            // 清除session
            session.invalidate();
            
            redirectAttributes.addFlashAttribute("successMessage", "账号已成功删除");
            return "redirect:/user/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "删除失败：" + e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * 显示偏好设置页面
     */
    @GetMapping("/preferences")
    public String showPreferencesPage(HttpSession session, Model model) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        // 获取所有偏好选项
        java.util.List<Preferences> allPreferences = preferenceService.getAllPreferences();
        model.addAttribute("allPreferences", allPreferences);

        // 获取用户当前的偏好
        java.util.List<Preferences> userPreferences = preferenceService.getUserPreferences(currentUser.getUserId());
        model.addAttribute("userPreferences", userPreferences);

        // 创建用户偏好ID集合，方便在页面中判断是否已选中
        java.util.Set<Integer> userPreferenceIds = userPreferences.stream()
                .map(Preferences::getPreferenceId)
                .collect(java.util.stream.Collectors.toSet());
        model.addAttribute("userPreferenceIds", userPreferenceIds);

        return "user/preferences";
    }

    /**
     * 保存用户偏好设置
     */
    @PostMapping("/preferences/save")
    public String savePreferences(@RequestParam(value = "preferenceIds", required = false) java.util.List<Integer> preferenceIds,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        try {
            // 设置用户偏好
            preferenceService.setUserPreferences(currentUser.getUserId(), preferenceIds);
            
            redirectAttributes.addFlashAttribute("successMessage", "偏好设置已保存！");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "保存失败：" + e.getMessage());
            return "redirect:/user/preferences";
        }
    }
}
