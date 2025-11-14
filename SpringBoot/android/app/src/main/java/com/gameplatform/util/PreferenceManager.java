package com.gameplatform.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences utility class for managing user data and app settings
 * Requirements: 2.3, 2.5
 */
public class PreferenceManager {
    
    private static final String PREF_NAME = "GamePlatformPrefs";
    private static PreferenceManager instance;
    private SharedPreferences sharedPreferences;
    
    // User login state keys
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_TOKEN_EXPIRES_AT = "token_expires_at";
    private static final String KEY_LOGIN_TIME = "login_time";
    
    // App settings keys
    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    
    // Login session duration (7 days in milliseconds)
    private static final long LOGIN_SESSION_DURATION = 7 * 24 * 60 * 60 * 1000L;
    
    private PreferenceManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }
    
    // User login state methods
    
    /**
     * Set user login status
     * @param isLoggedIn Login status
     */
    public void setLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }
    
    /**
     * Check if user is logged in and session is valid
     * @return true if logged in and session valid, false otherwise
     */
    public boolean isLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (!isLoggedIn) {
            return false;
        }
        
        // Check if login session is still valid
        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - loginTime > LOGIN_SESSION_DURATION) {
            // Session expired, clear login data
            clearLoginData();
            return false;
        }
        
        // Check if token is still valid
        long tokenExpiresAt = sharedPreferences.getLong(KEY_TOKEN_EXPIRES_AT, 0);
        if (tokenExpiresAt > 0 && currentTime > tokenExpiresAt) {
            // Token expired, clear login data
            clearLoginData();
            return false;
        }
        
        return true;
    }
    
    /**
     * Set user ID
     * @param userId User ID
     */
    public void setUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }
    
    /**
     * Get user ID
     * @return User ID or null if not set
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    
    /**
     * Set phone number
     * @param phoneNumber Phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply();
    }
    
    /**
     * Get phone number
     * @return Phone number or null if not set
     */
    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null);
    }
    
    /**
     * Set nickname
     * @param nickname User nickname
     */
    public void setNickname(String nickname) {
        sharedPreferences.edit().putString(KEY_NICKNAME, nickname).apply();
    }
    
    /**
     * Get nickname
     * @return Nickname or null if not set
     */
    public String getNickname() {
        return sharedPreferences.getString(KEY_NICKNAME, null);
    }
    
    /**
     * Set avatar URL
     * @param avatar Avatar URL
     */
    public void setAvatar(String avatar) {
        sharedPreferences.edit().putString(KEY_AVATAR, avatar).apply();
    }
    
    /**
     * Get avatar URL
     * @return Avatar URL or null if not set
     */
    public String getAvatar() {
        return sharedPreferences.getString(KEY_AVATAR, null);
    }
    
    /**
     * Set authentication token
     * @param token Authentication token
     */
    public void setAuthToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }
    
    /**
     * Get authentication token
     * @return Authentication token or null if not set
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }
    
    /**
     * Set token expiration time
     * @param expiresAt Token expiration timestamp
     */
    public void setTokenExpiresAt(long expiresAt) {
        sharedPreferences.edit().putLong(KEY_TOKEN_EXPIRES_AT, expiresAt).apply();
    }
    
    /**
     * Get token expiration time
     * @return Token expiration timestamp
     */
    public long getTokenExpiresAt() {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRES_AT, 0);
    }
    
    /**
     * Set login time
     * @param loginTime Login timestamp
     */
    public void setLoginTime(long loginTime) {
        sharedPreferences.edit().putLong(KEY_LOGIN_TIME, loginTime).apply();
    }
    
    /**
     * Get login time
     * @return Login timestamp
     */
    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
    }
    
    // App settings methods
    
    /**
     * Set auto login preference
     * @param autoLogin Auto login enabled
     */
    public void setAutoLogin(boolean autoLogin) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_LOGIN, autoLogin).apply();
    }
    
    /**
     * Check if auto login is enabled
     * @return true if auto login enabled, false otherwise
     */
    public boolean isAutoLoginEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_LOGIN, true); // Default to true
    }
    
    /**
     * Set notification preference
     * @param enabled Notification enabled
     */
    public void setNotificationEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }
    
    /**
     * Check if notifications are enabled
     * @return true if notifications enabled, false otherwise
     */
    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true); // Default to true
    }
    
    /**
     * Set first launch flag
     * @param firstLaunch First launch flag
     */
    public void setFirstLaunch(boolean firstLaunch) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply();
    }
    
    /**
     * Check if this is the first launch
     * @return true if first launch, false otherwise
     */
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true); // Default to true
    }
    
    // Utility methods
    
    /**
     * Clear all login data
     */
    public void clearLoginData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_PHONE_NUMBER);
        editor.remove(KEY_NICKNAME);
        editor.remove(KEY_AVATAR);
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRES_AT);
        editor.remove(KEY_LOGIN_TIME);
        editor.apply();
    }
    
    /**
     * Clear all preferences (logout and reset app)
     */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
    
    /**
     * Check if user login session is about to expire (within 1 day)
     * @return true if session expires soon, false otherwise
     */
    public boolean isSessionExpiringSoon() {
        if (!isLoggedIn()) {
            return false;
        }
        
        long loginTime = getLoginTime();
        long currentTime = System.currentTimeMillis();
        long timeRemaining = LOGIN_SESSION_DURATION - (currentTime - loginTime);
        
        // Check if less than 1 day remaining
        return timeRemaining < (24 * 60 * 60 * 1000L);
    }
    
    /**
     * Extend login session (refresh login time)
     */
    public void extendSession() {
        if (isLoggedIn()) {
            setLoginTime(System.currentTimeMillis());
        }
    }
    
    /**
     * Get remaining session time in milliseconds
     * @return Remaining session time or 0 if not logged in
     */
    public long getRemainingSessionTime() {
        if (!isLoggedIn()) {
            return 0;
        }
        
        long loginTime = getLoginTime();
        long currentTime = System.currentTimeMillis();
        long timeRemaining = LOGIN_SESSION_DURATION - (currentTime - loginTime);
        
        return Math.max(0, timeRemaining);
    }
    
    // User data management methods
    
    /**
     * Save current user data
     * @param user User object to save
     */
    public void saveCurrentUser(com.gameplatform.model.User user) {
        if (user == null) return;
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(KEY_NICKNAME, user.getNickname());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.putBoolean(KEY_IS_LOGGED_IN, user.isLoggedIn());
        editor.apply();
    }
    
    /**
     * Get current user data
     * @return User object or null if not available
     */
    public com.gameplatform.model.User getCurrentUser() {
        String userId = getUserId();
        if (userId == null) return null;
        
        com.gameplatform.model.User user = new com.gameplatform.model.User();
        user.setUserId(userId);
        user.setPhoneNumber(getPhoneNumber());
        user.setNickname(getNickname());
        user.setAvatar(getAvatar());
        user.setLoggedIn(isLoggedIn());
        user.setRegisterTime(getLoginTime()); // Use login time as register time for now
        
        return user;
    }
    
    /**
     * Clear all user data (for logout)
     */
    public void clearUserData() {
        clearLoginData();
        // Also clear any other user-specific settings if needed
        // Keep app-level settings like notification preferences
    }
    
    /**
     * Check if user data exists
     * @return true if user data exists, false otherwise
     */
    public boolean hasUserData() {
        return getUserId() != null && !getUserId().isEmpty();
    }
    
    // Static method for getting instance without context (for existing code compatibility)
    public static PreferenceManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PreferenceManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }
}