package com.graduation.exception;

/**
 * 用户名重复异常
 * 当注册时用户名已存在时抛出
 */
public class DuplicateUsernameException extends BusinessException {
    
    public DuplicateUsernameException(String username) {
        super("DUPLICATE_USERNAME", "用户名已存在: " + username);
    }
    
    public DuplicateUsernameException(String message, Throwable cause) {
        super("DUPLICATE_USERNAME", message, cause);
    }
}
