package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册DTO
 * 用于接收用户注册请求数据
 */
@Data
public class UserRegisterDTO {
    
    /**
     * 用户名（邮箱或手机号）
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(
        regexp = "^([a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9_\\-]+(\\.[a-zA-Z0-9_\\-]+)+|1[3-9]\\d{9})$",
        message = "用户名必须是有效的邮箱或手机号"
    )
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
}
