package com.gameplatform.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gameplatform.R;
import com.gameplatform.network.NetworkManager;
import com.gameplatform.ui.auth.AuthActivity;
import com.gameplatform.ui.main.MainActivity;
import com.gameplatform.util.CacheManager;
import com.gameplatform.util.PerformanceOptimizer;
import com.gameplatform.util.PreferenceManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enhanced Splash Activity with startup optimization and data preloading
 * Requirements: 2.3, 2.5
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final String TAG = "SplashActivity";
    private static final int MIN_SPLASH_DELAY = 1500; // Minimum 1.5 seconds
    private static final int MAX_SPLASH_DELAY = 3000; // Maximum 3 seconds
    
    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;
    private TextView statusText;
    private ExecutorService executorService;
    private long startTime;
    private boolean initializationComplete = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        startTime = System.currentTimeMillis();
        initializeViews();
        initializeApp();
    }
    
    /**
     * Initialize views
     */
    private void initializeViews() {
        progressBar = findViewById(R.id.progress_bar);
        statusText = findViewById(R.id.tv_status);
        
        if (statusText != null) {
            statusText.setText(R.string.splash_initializing);
        }
    }
    
    /**
     * Initialize application components
     */
    private void initializeApp() {
        preferenceManager = PreferenceManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Start initialization in background thread
        executorService.execute(this::performInitialization);
    }
    
    /**
     * Perform app initialization tasks in background
     */
    private void performInitialization() {
        try {
            Log.d(TAG, "Starting app initialization");
            
            // Update status on UI thread
            runOnUiThread(() -> updateStatus(R.string.splash_checking_network));
            
            // Initialize network manager
            NetworkManager.getInstance();
            Thread.sleep(200); // Small delay for visual feedback
            
            // Update status
            runOnUiThread(() -> updateStatus(R.string.splash_loading_cache));
            
            // Initialize cache manager and preload data
            CacheManager.getInstance(this);
            if (preferenceManager.isLoggedIn()) {
                preloadUserData();
            }
            Thread.sleep(300);
            
            // Update status
            runOnUiThread(() -> updateStatus(R.string.splash_preparing_ui));
            
            // Initialize performance optimizer
            PerformanceOptimizer.getInstance(this);
            PerformanceOptimizer.getInstance().optimizeStartup();
            Thread.sleep(200);
            
            // Mark initialization as complete
            initializationComplete = true;
            
            // Calculate remaining time to meet minimum splash duration
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = MIN_SPLASH_DELAY - elapsedTime;
            
            if (remainingTime > 0) {
                runOnUiThread(() -> updateStatus(R.string.splash_ready));
                Thread.sleep(remainingTime);
            }
            
            // Navigate to appropriate screen
            runOnUiThread(this::checkLoginStatusAndNavigate);
            
        } catch (InterruptedException e) {
            Log.e(TAG, "Initialization interrupted", e);
            Thread.currentThread().interrupt();
            runOnUiThread(this::checkLoginStatusAndNavigate);
        } catch (Exception e) {
            Log.e(TAG, "Error during initialization", e);
            runOnUiThread(this::checkLoginStatusAndNavigate);
        }
    }
    
    /**
     * Preload user data for faster app experience
     */
    private void preloadUserData() {
        try {
            Log.d(TAG, "Preloading user data");
            
            // Extend session if it's about to expire
            if (preferenceManager.isSessionExpiringSoon()) {
                Log.d(TAG, "Session expiring soon, extending session");
                preferenceManager.extendSession();
            }
            
            // Preload user profile data
            com.gameplatform.model.User currentUser = preferenceManager.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "User data preloaded for: " + currentUser.getNickname());
                
                // Cache user data for faster access
                CacheManager.getInstance().cacheUser(currentUser);
            }
            
            // Preload essential cached data
            CacheManager.getInstance().preloadEssentialData();
            
            // Initialize image cache (if using Glide or similar)
            // This would be where you'd warm up the image cache
            
        } catch (Exception e) {
            Log.e(TAG, "Error preloading user data", e);
        }
    }
    
    /**
     * Update status text on UI
     */
    private void updateStatus(int stringResId) {
        if (statusText != null) {
            statusText.setText(stringResId);
        }
    }
    
    /**
     * Check user login status and navigate accordingly
     */
    private void checkLoginStatusAndNavigate() {
        try {
            Log.d(TAG, "Checking login status");
            
            boolean isLoggedIn = preferenceManager.isLoggedIn();
            boolean autoLoginEnabled = preferenceManager.isAutoLoginEnabled();
            boolean isFirstLaunch = preferenceManager.isFirstLaunch();
            
            Log.d(TAG, "Login status - Logged in: " + isLoggedIn + 
                      ", Auto-login: " + autoLoginEnabled + 
                      ", First launch: " + isFirstLaunch);
            
            if (isFirstLaunch) {
                // Mark first launch as complete
                preferenceManager.setFirstLaunch(false);
            }
            
            if (autoLoginEnabled && isLoggedIn) {
                // User is logged in and auto-login is enabled, go to main screen
                Log.d(TAG, "Auto-login successful, navigating to main");
                navigateToMain();
            } else {
                // User is not logged in or auto-login is disabled, go to auth screen
                Log.d(TAG, "Navigating to authentication");
                navigateToAuth();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status", e);
            // Fallback to auth screen on error
            navigateToAuth();
        }
    }
    
    /**
     * Navigate to main activity with optimized intent flags
     */
    private void navigateToMain() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
            // Add any preloaded data to intent if needed
            if (preferenceManager.hasUserData()) {
                intent.putExtra("user_preloaded", true);
            }
            
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to main activity", e);
            navigateToAuth(); // Fallback
        }
    }
    
    /**
     * Navigate to authentication activity with optimized intent flags
     */
    private void navigateToAuth() {
        try {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to auth activity", e);
            finish(); // Close app if navigation fails
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        // Log performance summary
        try {
            PerformanceOptimizer.getInstance().logPerformanceSummary();
        } catch (Exception e) {
            Log.e(TAG, "Error logging performance summary", e);
        }
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button during splash screen
        // User should wait for initialization to complete
    }
}