package com.gameplatform.exception;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gameplatform.network.NetworkConfig;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * Global exception handler for unified error processing
 * Requirements: 1.3, 2.2, 3.2, 4.1, 5.1
 */
public class GlobalExceptionHandler {
    
    private static final String TAG = "GlobalExceptionHandler";
    
    /**
     * Handle exception and return user-friendly error message
     * @param context Context for showing toast
     * @param throwable Exception to handle
     * @return User-friendly error message
     */
    public static String handleException(Context context, Throwable throwable) {
        return handleException(context, throwable, true);
    }
    
    /**
     * Handle exception and optionally show toast
     * @param context Context for showing toast
     * @param throwable Exception to handle
     * @param showToast Whether to show toast message
     * @return User-friendly error message
     */
    public static String handleException(Context context, Throwable throwable, boolean showToast) {
        String errorMessage = getErrorMessage(throwable);
        
        // Log the error for debugging
        Log.e(TAG, "Exception handled: " + throwable.getClass().getSimpleName(), throwable);
        
        // Show toast if requested
        if (showToast && context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
        
        return errorMessage;
    }
    
    /**
     * Get user-friendly error message from throwable
     * @param throwable Exception to process
     * @return User-friendly error message
     */
    public static String getErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "未知错误";
        }
        
        // Handle custom exceptions
        if (throwable instanceof AuthException) {
            return ((AuthException) throwable).getUserFriendlyMessage();
        }
        
        if (throwable instanceof ValidationException) {
            return ((ValidationException) throwable).getUserFriendlyMessage();
        }
        
        if (throwable instanceof BusinessException) {
            return ((BusinessException) throwable).getUserFriendlyMessage();
        }
        
        if (throwable instanceof NetworkException) {
            NetworkException networkException = (NetworkException) throwable;
            if (networkException.isConnectionError()) {
                return "网络连接失败，请检查网络设置";
            } else if (networkException.isTimeoutError()) {
                return "网络请求超时，请稍后再试";
            } else if (networkException.isServerError()) {
                return "服务器错误，请稍后再试";
            } else if (networkException.isClientError()) {
                return "请求错误，请检查输入信息";
            } else {
                return "网络错误：" + networkException.getErrorMessage();
            }
        }
        
        // Handle HTTP exceptions
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int code = httpException.code();
            
            switch (code) {
                case NetworkConfig.BAD_REQUEST_CODE:
                    return "请求参数错误";
                case NetworkConfig.UNAUTHORIZED_CODE:
                    return "未授权访问，请重新登录";
                case NetworkConfig.FORBIDDEN_CODE:
                    return "访问被拒绝";
                case NetworkConfig.NOT_FOUND_CODE:
                    return "请求的资源不存在";
                case NetworkConfig.SERVER_ERROR_CODE:
                    return "服务器内部错误";
                default:
                    if (code >= 400 && code < 500) {
                        return "客户端错误 (" + code + ")";
                    } else if (code >= 500) {
                        return "服务器错误 (" + code + ")";
                    } else {
                        return "HTTP错误 (" + code + ")";
                    }
            }
        }
        
        // Handle network-related exceptions
        if (throwable instanceof ConnectException) {
            return "无法连接到服务器，请检查网络";
        }
        
        if (throwable instanceof SocketTimeoutException) {
            return "网络请求超时，请稍后再试";
        }
        
        if (throwable instanceof UnknownHostException) {
            return "网络连接失败，请检查网络设置";
        }
        
        // Handle other common exceptions
        if (throwable instanceof IllegalArgumentException) {
            return "参数错误";
        }
        
        if (throwable instanceof IllegalStateException) {
            return "状态异常";
        }
        
        if (throwable instanceof SecurityException) {
            return "权限不足";
        }
        
        // Default error message
        String message = throwable.getMessage();
        if (message != null && !message.isEmpty()) {
            return message;
        } else {
            return "操作失败，请稍后再试";
        }
    }
    
    /**
     * Convert throwable to appropriate custom exception
     * @param throwable Original throwable
     * @return Custom exception
     */
    public static Exception convertToCustomException(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int code = httpException.code();
            String message = getErrorMessage(throwable);
            
            if (code == NetworkConfig.UNAUTHORIZED_CODE) {
                return new AuthException(AuthException.AuthErrorType.UNAUTHORIZED, message, throwable);
            } else {
                return new NetworkException(code, message, throwable);
            }
        }
        
        if (throwable instanceof ConnectException ||
            throwable instanceof SocketTimeoutException ||
            throwable instanceof UnknownHostException) {
            return new NetworkException(getErrorMessage(throwable), throwable);
        }
        
        if (throwable instanceof IllegalArgumentException) {
            return new ValidationException(
                ValidationException.ValidationErrorType.INVALID_FORMAT,
                "unknown",
                getErrorMessage(throwable),
                throwable
            );
        }
        
        // Return as generic exception if no specific conversion applies
        return new Exception(getErrorMessage(throwable), throwable);
    }
    
    /**
     * Check if exception requires user re-authentication
     * @param throwable Exception to check
     * @return true if re-authentication required, false otherwise
     */
    public static boolean requiresReAuthentication(Throwable throwable) {
        if (throwable instanceof AuthException) {
            return ((AuthException) throwable).requiresReAuthentication();
        }
        
        if (throwable instanceof HttpException) {
            return ((HttpException) throwable).code() == NetworkConfig.UNAUTHORIZED_CODE;
        }
        
        return false;
    }
    
    /**
     * Check if exception is retryable
     * @param throwable Exception to check
     * @return true if retryable, false otherwise
     */
    public static boolean isRetryable(Throwable throwable) {
        if (throwable instanceof BusinessException) {
            return ((BusinessException) throwable).isRetryable();
        }
        
        if (throwable instanceof NetworkException) {
            NetworkException networkException = (NetworkException) throwable;
            return networkException.isTimeoutError() || networkException.isServerError();
        }
        
        if (throwable instanceof SocketTimeoutException ||
            throwable instanceof ConnectException) {
            return true;
        }
        
        if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            return code >= 500; // Server errors are retryable
        }
        
        return false;
    }
    
    /**
     * Check if exception requires data refresh
     * @param throwable Exception to check
     * @return true if refresh required, false otherwise
     */
    public static boolean requiresDataRefresh(Throwable throwable) {
        if (throwable instanceof BusinessException) {
            return ((BusinessException) throwable).requiresDataRefresh();
        }
        
        if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            return code == NetworkConfig.NOT_FOUND_CODE;
        }
        
        return false;
    }
    
    /**
     * Get error category for analytics or logging
     * @param throwable Exception to categorize
     * @return Error category string
     */
    public static String getErrorCategory(Throwable throwable) {
        if (throwable instanceof AuthException) {
            return "AUTH_ERROR";
        } else if (throwable instanceof ValidationException) {
            return "VALIDATION_ERROR";
        } else if (throwable instanceof BusinessException) {
            return "BUSINESS_ERROR";
        } else if (throwable instanceof NetworkException) {
            return "NETWORK_ERROR";
        } else if (throwable instanceof HttpException) {
            return "HTTP_ERROR";
        } else if (throwable instanceof ConnectException ||
                   throwable instanceof SocketTimeoutException ||
                   throwable instanceof UnknownHostException) {
            return "CONNECTIVITY_ERROR";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
}