package com.graduation.exception;

/**
 * 用户未找到异常
 * 当查询的用户不存在时抛出
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(String message) {
        super("USER_NOT_FOUND", message);
    }
    
    public UserNotFoundException(Integer userId) {
        super("USER_NOT_FOUND", "用户不存在: ID=" + userId);
    }
    
    public UserNotFoundException(String field, String value) {
        super("USER_NOT_FOUND", "用户不存在: " + field + "=" + value);
    }
}
