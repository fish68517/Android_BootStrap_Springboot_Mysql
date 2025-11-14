package com.gameplatform.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.gameplatform.exception.GlobalExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Error reporting utility for logging and crash reporting
 * Requirements: 1.3, 2.2, 3.2, 4.1, 5.1
 */
public class ErrorReportUtil {
    
    private static final String TAG = "ErrorReportUtil";
    private static final String ERROR_LOG_FILE = "error_log.txt";
    
    /**
     * Report error with context information
     * @param context Context
     * @param throwable Exception to report
     * @param userAction User action that caused the error
     */
    public static void reportError(Context context, Throwable throwable, String userAction) {
        reportError(context, throwable, userAction, null);
    }
    
    /**
     * Report error with context information and additional data
     * @param context Context
     * @param throwable Exception to report
     * @param userAction User action that caused the error
     * @param additionalData Additional data for debugging
     */
    public static void reportError(Context context, Throwable throwable, String userAction, String additionalData) {
        try {
            // Create error report
            ErrorReport errorReport = createErrorReport(context, throwable, userAction, additionalData);
            
            // Log to Android Log
            logError(errorReport);
            
            // Save to local file for debugging
            saveErrorToFile(context, errorReport);
            
            // In production, you might want to send to crash reporting service
            // sendToCrashReportingService(errorReport);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to report error", e);
        }
    }
    
    /**
     * Create detailed error report
     * @param context Context
     * @param throwable Exception
     * @param userAction User action
     * @param additionalData Additional data
     * @return Error report object
     */
    private static ErrorReport createErrorReport(Context context, Throwable throwable, 
                                               String userAction, String additionalData) {
        ErrorReport report = new ErrorReport();
        
        // Basic error information
        report.timestamp = System.currentTimeMillis();
        report.errorMessage = GlobalExceptionHandler.getErrorMessage(throwable);
        report.errorCategory = GlobalExceptionHandler.getErrorCategory(throwable);
        report.userAction = userAction;
        report.additionalData = additionalData;
        
        // Exception details
        if (throwable != null) {
            report.exceptionClass = throwable.getClass().getSimpleName();
            report.stackTrace = getStackTrace(throwable);
            report.causeMessage = throwable.getCause() != null ? throwable.getCause().getMessage() : null;
        }
        
        // Device information
        report.deviceModel = Build.MODEL;
        report.deviceManufacturer = Build.MANUFACTURER;
        report.androidVersion = Build.VERSION.RELEASE;
        report.apiLevel = Build.VERSION.SDK_INT;
        
        // App information
        if (context != null) {
            try {
                report.appVersion = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                report.appVersionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (Exception e) {
                Log.w(TAG, "Failed to get app version info", e);
            }
        }
        
        // User information (if available)
        try {
            PreferenceManager prefManager = PreferenceManager.getInstance();
            if (prefManager.isLoggedIn()) {
                report.userId = prefManager.getUserId();
                report.userPhoneNumber = ValidationUtil.maskPhoneNumber(prefManager.getPhoneNumber());
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get user info", e);
        }
        
        return report;
    }
    
    /**
     * Log error to Android Log
     * @param errorReport Error report to log
     */
    private static void logError(ErrorReport errorReport) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Error Report:\n");
        logMessage.append("Timestamp: ").append(formatTimestamp(errorReport.timestamp)).append("\n");
        logMessage.append("Category: ").append(errorReport.errorCategory).append("\n");
        logMessage.append("Message: ").append(errorReport.errorMessage).append("\n");
        logMessage.append("User Action: ").append(errorReport.userAction).append("\n");
        logMessage.append("Exception: ").append(errorReport.exceptionClass).append("\n");
        logMessage.append("Device: ").append(errorReport.deviceManufacturer)
                .append(" ").append(errorReport.deviceModel).append("\n");
        logMessage.append("Android: ").append(errorReport.androidVersion)
                .append(" (API ").append(errorReport.apiLevel).append(")\n");
        logMessage.append("App Version: ").append(errorReport.appVersion).append("\n");
        
        if (errorReport.additionalData != null) {
            logMessage.append("Additional Data: ").append(errorReport.additionalData).append("\n");
        }
        
        if (errorReport.stackTrace != null) {
            logMessage.append("Stack Trace:\n").append(errorReport.stackTrace);
        }
        
        Log.e(TAG, logMessage.toString());
    }
    
    /**
     * Save error report to local file
     * @param context Context
     * @param errorReport Error report to save
     */
    private static void saveErrorToFile(Context context, ErrorReport errorReport) {
        try {
            String errorLog = formatErrorReport(errorReport);
            
            // Append to error log file
            java.io.File errorFile = new java.io.File(context.getFilesDir(), ERROR_LOG_FILE);
            java.io.FileWriter writer = new java.io.FileWriter(errorFile, true);
            writer.append(errorLog);
            writer.append("\n---\n");
            writer.close();
            
            // Keep only recent errors (limit file size)
            limitErrorLogSize(errorFile);
            
        } catch (Exception e) {
            Log.w(TAG, "Failed to save error to file", e);
        }
    }
    
    /**
     * Format error report as string
     * @param errorReport Error report to format
     * @return Formatted error report string
     */
    private static String formatErrorReport(ErrorReport errorReport) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error Report - ").append(formatTimestamp(errorReport.timestamp)).append("\n");
        sb.append("Category: ").append(errorReport.errorCategory).append("\n");
        sb.append("Message: ").append(errorReport.errorMessage).append("\n");
        sb.append("User Action: ").append(errorReport.userAction).append("\n");
        sb.append("Exception: ").append(errorReport.exceptionClass).append("\n");
        sb.append("Device: ").append(errorReport.deviceManufacturer)
                .append(" ").append(errorReport.deviceModel).append("\n");
        sb.append("Android: ").append(errorReport.androidVersion)
                .append(" (API ").append(errorReport.apiLevel).append(")\n");
        sb.append("App: ").append(errorReport.appVersion)
                .append(" (").append(errorReport.appVersionCode).append(")\n");
        
        if (errorReport.userId != null) {
            sb.append("User ID: ").append(errorReport.userId).append("\n");
        }
        
        if (errorReport.userPhoneNumber != null) {
            sb.append("Phone: ").append(errorReport.userPhoneNumber).append("\n");
        }
        
        if (errorReport.additionalData != null) {
            sb.append("Additional Data: ").append(errorReport.additionalData).append("\n");
        }
        
        if (errorReport.causeMessage != null) {
            sb.append("Cause: ").append(errorReport.causeMessage).append("\n");
        }
        
        if (errorReport.stackTrace != null) {
            sb.append("Stack Trace:\n").append(errorReport.stackTrace).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Get stack trace as string
     * @param throwable Exception
     * @return Stack trace string
     */
    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Format timestamp for display
     * @param timestamp Timestamp in milliseconds
     * @return Formatted timestamp string
     */
    private static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Limit error log file size by keeping only recent entries
     * @param errorFile Error log file
     */
    private static void limitErrorLogSize(java.io.File errorFile) {
        try {
            long maxSize = 1024 * 1024; // 1MB
            if (errorFile.length() > maxSize) {
                // Read all content
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(errorFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                
                // Keep only the last half of the content
                String fullContent = content.toString();
                String truncatedContent = fullContent.substring(fullContent.length() / 2);
                
                // Write back truncated content
                java.io.FileWriter writer = new java.io.FileWriter(errorFile, false);
                writer.write("... (truncated) ...\n");
                writer.write(truncatedContent);
                writer.close();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to limit error log size", e);
        }
    }
    
    /**
     * Get error log content for debugging
     * @param context Context
     * @return Error log content or null if not available
     */
    public static String getErrorLog(Context context) {
        try {
            java.io.File errorFile = new java.io.File(context.getFilesDir(), ERROR_LOG_FILE);
            if (!errorFile.exists()) {
                return null;
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(errorFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            
            return content.toString();
        } catch (Exception e) {
            Log.w(TAG, "Failed to read error log", e);
            return null;
        }
    }
    
    /**
     * Clear error log
     * @param context Context
     */
    public static void clearErrorLog(Context context) {
        try {
            java.io.File errorFile = new java.io.File(context.getFilesDir(), ERROR_LOG_FILE);
            if (errorFile.exists()) {
                errorFile.delete();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to clear error log", e);
        }
    }
    
    /**
     * Error report data class
     */
    private static class ErrorReport {
        long timestamp;
        String errorMessage;
        String errorCategory;
        String userAction;
        String additionalData;
        String exceptionClass;
        String stackTrace;
        String causeMessage;
        String deviceModel;
        String deviceManufacturer;
        String androidVersion;
        int apiLevel;
        String appVersion;
        int appVersionCode;
        String userId;
        String userPhoneNumber;
    }
}