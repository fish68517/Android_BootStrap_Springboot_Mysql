package com.graduation.exception;

/**
 * 无效状态异常
 * 当状态值不合法时抛出
 */
public class InvalidStatusException extends BusinessException {
    
    public InvalidStatusException(String message) {
        super("INVALID_STATUS", message);
    }
    
    public InvalidStatusException(String entity, String status) {
        super("INVALID_STATUS", entity + "的状态无效: " + status);
    }
}
