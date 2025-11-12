package com.graduation.exception;

/**
 * 未授权异常
 * 当用户没有权限执行某操作时抛出
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
    
    public UnauthorizedException() {
        super("UNAUTHORIZED", "您没有权限执行此操作");
    }
}
