package com.gameplatform.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Data validation utility class
 * Requirements: 1.3, 2.2
 */
public class ValidationUtil {
    
    // Phone number pattern for Chinese mobile numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // Verification code pattern (6 digits)
    private static final Pattern VERIFICATION_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    
    // Nickname pattern (2-20 characters, Chinese, English, numbers, underscores)
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$");
    
    // Password pattern (6-20 characters, at least one letter and one number)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$");
    
    /**
     * Validate Chinese mobile phone number
     * @param phoneNumber Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }
    
    /**
     * Validate verification code
     * @param code Verification code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidVerificationCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        return VERIFICATION_CODE_PATTERN.matcher(code.trim()).matches();
    }
    
    /**
     * Validate nickname
     * @param nickname Nickname to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidNickname(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return false;
        }
        String trimmed = nickname.trim();
        return !TextUtils.isEmpty(trimmed) && NICKNAME_PATTERN.matcher(trimmed).matches();
    }
    
    /**
     * Validate password
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate email address
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
    
    /**
     * Check if string is empty or null
     * @param str String to check
     * @return true if empty or null, false otherwise
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim());
    }
    
    /**
     * Check if string length is within range
     * @param str String to check
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if within range, false otherwise
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (TextUtils.isEmpty(str)) {
            return minLength == 0;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate game ID format
     * @param gameId Game ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGameId(String gameId) {
        if (TextUtils.isEmpty(gameId)) {
            return false;
        }
        // Game ID should be alphanumeric and at least 1 character
        return gameId.trim().matches("^[a-zA-Z0-9]+$") && gameId.trim().length() > 0;
    }
    
    /**
     * Validate user ID format
     * @param userId User ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUserId(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        // User ID should be alphanumeric and at least 1 character
        return userId.trim().matches("^[a-zA-Z0-9]+$") && userId.trim().length() > 0;
    }
    
    /**
     * Validate comment content
     * @param content Comment content to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCommentContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        String trimmed = content.trim();
        // Comment should be 1-500 characters
        return !TextUtils.isEmpty(trimmed) && trimmed.length() >= 1 && trimmed.length() <= 500;
    }
    
    /**
     * Validate URL format
     * @param url URL to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return Patterns.WEB_URL.matcher(url.trim()).matches();
    }
    
    /**
     * Get phone number validation error message
     * @param phoneNumber Phone number to validate
     * @return Error message or null if valid
     */
    public static String getPhoneNumberError(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "手机号不能为空";
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            return "请输入正确的手机号";
        }
        return null;
    }
    
    /**
     * Get verification code validation error message
     * @param code Verification code to validate
     * @return Error message or null if valid
     */
    public static String getVerificationCodeError(String code) {
        if (TextUtils.isEmpty(code)) {
            return "验证码不能为空";
        }
        if (!isValidVerificationCode(code)) {
            return "请输入6位数字验证码";
        }
        return null;
    }
    
    /**
     * Get nickname validation error message
     * @param nickname Nickname to validate
     * @return Error message or null if valid
     */
    public static String getNicknameError(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return "昵称不能为空";
        }
        if (!isValidNickname(nickname)) {
            return "昵称长度为2-20个字符，支持中文、英文、数字和下划线";
        }
        return null;
    }
    
    /**
     * Get comment content validation error message
     * @param content Comment content to validate
     * @return Error message or null if valid
     */
    public static String getCommentContentError(String content) {
        if (TextUtils.isEmpty(content)) {
            return "评论内容不能为空";
        }
        if (!isValidCommentContent(content)) {
            return "评论内容长度应在1-500个字符之间";
        }
        return null;
    }
    
    /**
     * Sanitize input string (remove leading/trailing spaces and dangerous characters)
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        
        // Remove leading/trailing spaces
        String sanitized = input.trim();
        
        // Remove or replace potentially dangerous characters
        sanitized = sanitized.replaceAll("[<>\"'&]", "");
        
        return sanitized;
    }
    
    /**
     * Format phone number for display (add spaces for readability)
     * @param phoneNumber Phone number to format
     * @return Formatted phone number
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            return phoneNumber;
        }
        
        // Format as: 138 0013 8000
        return phoneNumber.substring(0, 3) + " " + 
               phoneNumber.substring(3, 7) + " " + 
               phoneNumber.substring(7);
    }
    
    /**
     * Mask phone number for privacy (show only first 3 and last 4 digits)
     * @param phoneNumber Phone number to mask
     * @return Masked phone number
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            return phoneNumber;
        }
        
        // Format as: 138****8000
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }
}