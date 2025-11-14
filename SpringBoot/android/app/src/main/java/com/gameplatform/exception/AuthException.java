package com.gameplatform.exception;

/**
 * Exception for authentication-related errors
 * Requirements: 1.3, 2.2
 */
public class AuthException extends Exception {
    
    public enum AuthErrorType {
        INVALID_PHONE_NUMBER,
        INVALID_VERIFICATION_CODE,
        VERIFICATION_CODE_EXPIRED,
        VERIFICATION_CODE_LIMIT_EXCEEDED,
        USER_NOT_FOUND,
        USER_ALREADY_EXISTS,
        TOKEN_EXPIRED,
        TOKEN_INVALID,
        LOGIN_FAILED,
        REGISTRATION_FAILED,
        SESSION_EXPIRED,
        UNAUTHORIZED
    }
    
    private AuthErrorType errorType;
    private String errorMessage;
    
    public AuthException(AuthErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.errorMessage = message;
    }
    
    public AuthException(AuthErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.errorMessage = message;
    }
    
    public AuthErrorType getErrorType() {
        return errorType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get user-friendly error message in Chinese
     * @return User-friendly error message
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case INVALID_PHONE_NUMBER:
                return "手机号格式不正确";
            case INVALID_VERIFICATION_CODE:
                return "验证码不正确";
            case VERIFICATION_CODE_EXPIRED:
                return "验证码已过期，请重新获取";
            case VERIFICATION_CODE_LIMIT_EXCEEDED:
                return "验证码错误次数过多，请稍后再试";
            case USER_NOT_FOUND:
                return "用户不存在，请先注册";
            case USER_ALREADY_EXISTS:
                return "用户已存在，请直接登录";
            case TOKEN_EXPIRED:
                return "登录已过期，请重新登录";
            case TOKEN_INVALID:
                return "登录状态异常，请重新登录";
            case LOGIN_FAILED:
                return "登录失败，请检查手机号和验证码";
            case REGISTRATION_FAILED:
                return "注册失败，请稍后再试";
            case SESSION_EXPIRED:
                return "会话已过期，请重新登录";
            case UNAUTHORIZED:
                return "没有访问权限";
            default:
                return errorMessage != null ? errorMessage : "认证失败";
        }
    }
    
    /**
     * Check if this error requires re-authentication
     * @return true if re-authentication required, false otherwise
     */
    public boolean requiresReAuthentication() {
        return errorType == AuthErrorType.TOKEN_EXPIRED ||
               errorType == AuthErrorType.TOKEN_INVALID ||
               errorType == AuthErrorType.SESSION_EXPIRED ||
               errorType == AuthErrorType.UNAUTHORIZED;
    }
    
    /**
     * Check if this error is recoverable by user action
     * @return true if recoverable, false otherwise
     */
    public boolean isRecoverable() {
        return errorType == AuthErrorType.INVALID_PHONE_NUMBER ||
               errorType == AuthErrorType.INVALID_VERIFICATION_CODE ||
               errorType == AuthErrorType.USER_NOT_FOUND ||
               errorType == AuthErrorType.USER_ALREADY_EXISTS;
    }
    
    @Override
    public String toString() {
        return "AuthException{" +
                "errorType=" + errorType +
                ", errorMessage='" + errorMessage + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}