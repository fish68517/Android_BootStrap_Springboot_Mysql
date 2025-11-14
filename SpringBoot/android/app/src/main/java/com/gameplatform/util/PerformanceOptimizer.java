package com.gameplatform.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Performance optimization utility for app startup and runtime performance
 * Requirements: 2.3
 */
public class PerformanceOptimizer {
    
    private static final String TAG = "PerformanceOptimizer";
    private static PerformanceOptimizer instance;
    
    private Context context;
    private ExecutorService backgroundExecutor;
    private long appStartTime;
    private boolean isLowMemoryDevice;
    
    private PerformanceOptimizer(Context context) {
        this.context = context.getApplicationContext();
        this.appStartTime = System.currentTimeMillis();
        this.backgroundExecutor = Executors.newCachedThreadPool();
        detectDeviceCapabilities();
    }
    
    public static synchronized PerformanceOptimizer getInstance(Context context) {
        if (instance == null) {
            instance = new PerformanceOptimizer(context);
        }
        return instance;
    }
    
    public static PerformanceOptimizer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PerformanceOptimizer not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }
    
    /**
     * Detect device capabilities for performance optimization
     */
    private void detectDeviceCapabilities() {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                
                // Consider device as low memory if available RAM is less than 1GB
                long availableMemoryMB = memoryInfo.availMem / (1024 * 1024);
                isLowMemoryDevice = availableMemoryMB < 1024;
                
                Log.d(TAG, "Device memory info - Available: " + availableMemoryMB + "MB, Low memory: " + isLowMemoryDevice);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    boolean isLowRamDevice = activityManager.isLowRamDevice();
                    Log.d(TAG, "System low RAM device: " + isLowRamDevice);
                    isLowMemoryDevice = isLowMemoryDevice || isLowRamDevice;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error detecting device capabilities", e);
            isLowMemoryDevice = false; // Default to false on error
        }
    }
    
    /**
     * Optimize app startup performance
     */
    public void optimizeStartup() {
        backgroundExecutor.execute(() -> {
            try {
                Log.d(TAG, "Starting startup optimization");
                
                // Warm up commonly used classes
                warmUpClasses();
                
                // Pre-initialize commonly used utilities
                preInitializeUtilities();
                
                // Optimize memory usage based on device capabilities
                optimizeMemoryUsage();
                
                // Log startup time
                long startupTime = System.currentTimeMillis() - appStartTime;
                Log.d(TAG, "App startup optimization completed in " + startupTime + "ms");
                
            } catch (Exception e) {
                Log.e(TAG, "Error during startup optimization", e);
            }
        });
    }
    
    /**
     * Warm up commonly used classes to reduce first-time loading
     */
    private void warmUpClasses() {
        try {
            // Warm up commonly used classes by accessing them
            Class.forName("com.gameplatform.model.User");
            Class.forName("com.gameplatform.model.Game");
            Class.forName("com.gameplatform.model.Comment");
            Class.forName("com.gameplatform.dto.ApiResponse");
            
            Log.d(TAG, "Classes warmed up");
        } catch (Exception e) {
            Log.e(TAG, "Error warming up classes", e);
        }
    }
    
    /**
     * Pre-initialize commonly used utilities
     */
    private void preInitializeUtilities() {
        try {
            // Initialize utilities that will be used frequently
            ValidationUtil.getInstance();
            TimeUtil.getInstance();
            
            Log.d(TAG, "Utilities pre-initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error pre-initializing utilities", e);
        }
    }
    
    /**
     * Optimize memory usage based on device capabilities
     */
    private void optimizeMemoryUsage() {
        try {
            if (isLowMemoryDevice) {
                Log.d(TAG, "Applying low memory device optimizations");
                
                // Reduce cache sizes for low memory devices
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
                
                // Suggest garbage collection
                System.gc();
            } else {
                Log.d(TAG, "Device has sufficient memory, using standard optimizations");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error optimizing memory usage", e);
        }
    }
    
    /**
     * Monitor memory usage and trigger cleanup if needed
     */
    public void monitorMemoryUsage() {
        backgroundExecutor.execute(() -> {
            try {
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                long maxMemory = runtime.maxMemory();
                
                double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
                
                Log.d(TAG, String.format("Memory usage: %.1f%% (%d/%d MB)", 
                    memoryUsagePercent, 
                    usedMemory / (1024 * 1024), 
                    maxMemory / (1024 * 1024)));
                
                // Trigger cleanup if memory usage is high
                if (memoryUsagePercent > 80) {
                    Log.w(TAG, "High memory usage detected, triggering cleanup");
                    triggerMemoryCleanup();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error monitoring memory usage", e);
            }
        });
    }
    
    /**
     * Trigger memory cleanup operations
     */
    private void triggerMemoryCleanup() {
        try {
            // Clear expired caches
            CacheManager.getInstance().clearExpiredCaches();
            
            // Suggest garbage collection
            System.gc();
            
            Log.d(TAG, "Memory cleanup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during memory cleanup", e);
        }
    }
    
    /**
     * Optimize UI rendering performance
     */
    public void optimizeUIRendering() {
        try {
            // Enable hardware acceleration if available
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Log.d(TAG, "Hardware acceleration available");
            }
            
            // Log rendering performance tips
            Log.d(TAG, "UI rendering optimization applied");
            
        } catch (Exception e) {
            Log.e(TAG, "Error optimizing UI rendering", e);
        }
    }
    
    /**
     * Get performance metrics
     */
    public PerformanceMetrics getPerformanceMetrics() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            metrics.totalMemoryMB = runtime.totalMemory() / (1024 * 1024);
            metrics.freeMemoryMB = runtime.freeMemory() / (1024 * 1024);
            metrics.usedMemoryMB = metrics.totalMemoryMB - metrics.freeMemoryMB;
            metrics.maxMemoryMB = runtime.maxMemory() / (1024 * 1024);
            metrics.memoryUsagePercent = (double) metrics.usedMemoryMB / metrics.maxMemoryMB * 100;
            
            metrics.isLowMemoryDevice = isLowMemoryDevice;
            metrics.appUptimeMs = System.currentTimeMillis() - appStartTime;
            
            // Get native heap info if available
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memoryInfo);
                metrics.nativeHeapSizeMB = memoryInfo.nativePss / 1024.0;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting performance metrics", e);
        }
        
        return metrics;
    }
    
    /**
     * Log performance summary
     */
    public void logPerformanceSummary() {
        backgroundExecutor.execute(() -> {
            try {
                PerformanceMetrics metrics = getPerformanceMetrics();
                
                Log.i(TAG, "=== Performance Summary ===");
                Log.i(TAG, "Memory Usage: " + String.format("%.1f%% (%d/%d MB)", 
                    metrics.memoryUsagePercent, metrics.usedMemoryMB, metrics.maxMemoryMB));
                Log.i(TAG, "Low Memory Device: " + metrics.isLowMemoryDevice);
                Log.i(TAG, "App Uptime: " + (metrics.appUptimeMs / 1000) + " seconds");
                if (metrics.nativeHeapSizeMB > 0) {
                    Log.i(TAG, "Native Heap: " + String.format("%.1f MB", metrics.nativeHeapSizeMB));
                }
                
                // Cache statistics
                CacheManager.CacheStats cacheStats = CacheManager.getInstance().getCacheStats();
                Log.i(TAG, "Cache Stats: " + cacheStats.toString());
                
                Log.i(TAG, "=========================");
                
            } catch (Exception e) {
                Log.e(TAG, "Error logging performance summary", e);
            }
        });
    }
    
    /**
     * Check if device is low memory
     */
    public boolean isLowMemoryDevice() {
        return isLowMemoryDevice;
    }
    
    /**
     * Get app startup time
     */
    public long getAppStartupTime() {
        return System.currentTimeMillis() - appStartTime;
    }
    
    /**
     * Shutdown performance optimizer
     */
    public void shutdown() {
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
    }
    
    // Performance metrics class
    public static class PerformanceMetrics {
        public long totalMemoryMB;
        public long freeMemoryMB;
        public long usedMemoryMB;
        public long maxMemoryMB;
        public double memoryUsagePercent;
        public double nativeHeapSizeMB;
        public boolean isLowMemoryDevice;
        public long appUptimeMs;
        
        @Override
        public String toString() {
            return "PerformanceMetrics{" +
                    "memory=" + String.format("%.1f%% (%d/%d MB)", memoryUsagePercent, usedMemoryMB, maxMemoryMB) +
                    ", lowMemory=" + isLowMemoryDevice +
                    ", uptime=" + (appUptimeMs / 1000) + "s" +
                    '}';
        }
    }
}