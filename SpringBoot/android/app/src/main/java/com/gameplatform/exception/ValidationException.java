package com.gameplatform.exception;

/**
 * Exception for data validation errors
 * Requirements: 1.3, 2.2
 */
public class ValidationException extends Exception {
    
    public enum ValidationErrorType {
        EMPTY_FIELD,
        INVALID_FORMAT,
        LENGTH_EXCEEDED,
        LENGTH_TOO_SHORT,
        INVALID_CHARACTERS,
        DUPLICATE_VALUE,
        REQUIRED_FIELD_MISSING,
        INVALID_RANGE
    }
    
    private ValidationErrorType errorType;
    private String fieldName;
    private String errorMessage;
    
    public ValidationException(ValidationErrorType errorType, String fieldName, String message) {
        super(message);
        this.errorType = errorType;
        this.fieldName = fieldName;
        this.errorMessage = message;
    }
    
    public ValidationException(ValidationErrorType errorType, String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.fieldName = fieldName;
        this.errorMessage = message;
    }
    
    public ValidationErrorType getErrorType() {
        return errorType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get user-friendly error message in Chinese
     * @return User-friendly error message
     */
    public String getUserFriendlyMessage() {
        String fieldDisplayName = getFieldDisplayName(fieldName);
        
        switch (errorType) {
            case EMPTY_FIELD:
                return fieldDisplayName + "不能为空";
            case INVALID_FORMAT:
                return fieldDisplayName + "格式不正确";
            case LENGTH_EXCEEDED:
                return fieldDisplayName + "长度超出限制";
            case LENGTH_TOO_SHORT:
                return fieldDisplayName + "长度不足";
            case INVALID_CHARACTERS:
                return fieldDisplayName + "包含无效字符";
            case DUPLICATE_VALUE:
                return fieldDisplayName + "已存在";
            case REQUIRED_FIELD_MISSING:
                return "缺少必填字段：" + fieldDisplayName;
            case INVALID_RANGE:
                return fieldDisplayName + "超出有效范围";
            default:
                return errorMessage != null ? errorMessage : "数据验证失败";
        }
    }
    
    /**
     * Get display name for field
     * @param fieldName Field name
     * @return Display name in Chinese
     */
    private String getFieldDisplayName(String fieldName) {
        if (fieldName == null) return "字段";
        
        switch (fieldName.toLowerCase()) {
            case "phonenumber":
            case "phone":
                return "手机号";
            case "verificationcode":
            case "code":
                return "验证码";
            case "nickname":
                return "昵称";
            case "password":
                return "密码";
            case "email":
                return "邮箱";
            case "content":
                return "内容";
            case "comment":
                return "评论";
            case "gameid":
                return "游戏ID";
            case "userid":
                return "用户ID";
            default:
                return fieldName;
        }
    }
    
    @Override
    public String toString() {
        return "ValidationException{" +
                "errorType=" + errorType +
                ", fieldName='" + fieldName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}