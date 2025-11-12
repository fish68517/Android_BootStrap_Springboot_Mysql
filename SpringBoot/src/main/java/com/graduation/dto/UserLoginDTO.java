package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 * 用于接收用户登录请求数据
 */
@Data
public class UserLoginDTO {
    
    /**
     * 用户名（邮箱或手机号）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
