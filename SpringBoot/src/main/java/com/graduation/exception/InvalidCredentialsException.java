package com.graduation.exception;

/**
 * 无效凭证异常
 * 当用户登录凭证不正确时抛出
 */
public class InvalidCredentialsException extends BusinessException {
    
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "用户名或密码错误");
    }
    
    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
}
