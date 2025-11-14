package com.gameplatform.exception;

/**
 * Exception for business logic errors
 * Requirements: 1.3, 2.2, 3.2, 4.1, 5.1
 */
public class BusinessException extends Exception {
    
    public enum BusinessErrorType {
        GAME_NOT_FOUND,
        USER_NOT_FOUND,
        COMMENT_NOT_FOUND,
        FAVORITE_ALREADY_EXISTS,
        FAVORITE_NOT_FOUND,
        COMMENT_ALREADY_LIKED,
        COMMENT_NOT_LIKED,
        OPERATION_NOT_ALLOWED,
        RESOURCE_LIMIT_EXCEEDED,
        SERVICE_UNAVAILABLE,
        DATA_INCONSISTENCY,
        CONCURRENT_MODIFICATION
    }
    
    private BusinessErrorType errorType;
    private String errorMessage;
    private Object relatedData;
    
    public BusinessException(BusinessErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.errorMessage = message;
    }
    
    public BusinessException(BusinessErrorType errorType, String message, Object relatedData) {
        super(message);
        this.errorType = errorType;
        this.errorMessage = message;
        this.relatedData = relatedData;
    }
    
    public BusinessException(BusinessErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.errorMessage = message;
    }
    
    public BusinessErrorType getErrorType() {
        return errorType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public Object getRelatedData() {
        return relatedData;
    }
    
    /**
     * Get user-friendly error message in Chinese
     * @return User-friendly error message
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case GAME_NOT_FOUND:
                return "游戏不存在或已下架";
            case USER_NOT_FOUND:
                return "用户不存在";
            case COMMENT_NOT_FOUND:
                return "评论不存在或已删除";
            case FAVORITE_ALREADY_EXISTS:
                return "已收藏该游戏";
            case FAVORITE_NOT_FOUND:
                return "未收藏该游戏";
            case COMMENT_ALREADY_LIKED:
                return "已点赞该评论";
            case COMMENT_NOT_LIKED:
                return "未点赞该评论";
            case OPERATION_NOT_ALLOWED:
                return "操作不被允许";
            case RESOURCE_LIMIT_EXCEEDED:
                return "资源使用超出限制";
            case SERVICE_UNAVAILABLE:
                return "服务暂时不可用，请稍后再试";
            case DATA_INCONSISTENCY:
                return "数据异常，请刷新后重试";
            case CONCURRENT_MODIFICATION:
                return "数据已被其他用户修改，请刷新后重试";
            default:
                return errorMessage != null ? errorMessage : "操作失败";
        }
    }
    
    /**
     * Check if this error is retryable
     * @return true if retryable, false otherwise
     */
    public boolean isRetryable() {
        return errorType == BusinessErrorType.SERVICE_UNAVAILABLE ||
               errorType == BusinessErrorType.CONCURRENT_MODIFICATION;
    }
    
    /**
     * Check if this error requires data refresh
     * @return true if refresh required, false otherwise
     */
    public boolean requiresDataRefresh() {
        return errorType == BusinessErrorType.DATA_INCONSISTENCY ||
               errorType == BusinessErrorType.CONCURRENT_MODIFICATION ||
               errorType == BusinessErrorType.GAME_NOT_FOUND ||
               errorType == BusinessErrorType.COMMENT_NOT_FOUND;
    }
    
    @Override
    public String toString() {
        return "BusinessException{" +
                "errorType=" + errorType +
                ", errorMessage='" + errorMessage + '\'' +
                ", relatedData=" + relatedData +
                ", cause=" + getCause() +
                '}';
    }
}