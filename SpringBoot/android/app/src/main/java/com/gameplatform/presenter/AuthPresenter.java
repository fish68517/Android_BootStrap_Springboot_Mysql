package com.gameplatform.presenter;

import android.text.TextUtils;
import java.util.regex.Pattern;

import com.gameplatform.base.BasePresenter;
import com.gameplatform.base.BaseRepository;
import com.gameplatform.contract.AuthContract;
import com.gameplatform.dto.response.LoginResponse;
import com.gameplatform.dto.response.RegisterResponse;
import com.gameplatform.repository.AuthRepository;
import com.gameplatform.util.PreferenceManager;

/**
 * Presenter for authentication operations
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class AuthPresenter extends BasePresenter implements AuthContract.Presenter {
    
    private AuthContract.View view;
    private AuthRepository repository;
    private PreferenceManager preferenceManager;
    
    // Phone number validation pattern (Chinese mobile numbers)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // Verification code validation pattern (6 digits)
    private static final Pattern CODE_PATTERN = Pattern.compile("^\\d{6}$");
    
    // Nickname validation pattern (2-20 characters, Chinese, English, numbers)
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]{2,20}$");
    
    // Verification code error attempt tracking
    private int codeErrorAttempts = 0;
    private static final int MAX_CODE_ATTEMPTS = 3;
    private long lockoutEndTime = 0;
    private static final long LOCKOUT_DURATION = 60 * 1000; // 60 seconds
    
    public AuthPresenter(AuthContract.View view, android.content.Context context) {
        this.view = view;
        this.repository = new AuthRepository();
        this.preferenceManager = PreferenceManager.getInstance(context);
    }
    
    @Override
    public void sendVerificationCode(String phoneNumber, String type) {
        if (view == null) return;
        
        // Check if currently locked out
        if (isLockedOut()) {
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            view.onVerificationCodeError("验证码错误次数过多，请" + remainingTime + "秒后重试");
            return;
        }
        
        view.showLoading();
        
        repository.sendVerificationCode(phoneNumber, type, new BaseRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (view != null) {
                    view.hideLoading();
                    view.onVerificationCodeSent();
                    view.startVerificationCodeCountdown(60);
                }
            }
            
            @Override
            public void onError(String message) {
                if (view != null) {
                    view.hideLoading();
                    view.onVerificationCodeError(message);
                }
            }
        });
    }
    
    @Override
    public void login(String phoneNumber, String verificationCode) {
        if (view == null) return;
        
        // Check if currently locked out
        if (isLockedOut()) {
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            view.onLoginError("验证码错误次数过多，请" + remainingTime + "秒后重试");
            return;
        }
        
        view.showLoading();
        
        repository.login(phoneNumber, verificationCode, new BaseRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                if (view != null) {
                    view.hideLoading();
                    
                    // Reset error attempts on successful login
                    resetCodeErrorAttempts();
                    
                    // Save login information
                    saveLoginInfo(response);
                    
                    view.onLoginSuccess(response.getUser());
                }
            }
            
            @Override
            public void onError(String message) {
                if (view != null) {
                    view.hideLoading();
                    
                    // Handle verification code errors
                    if (message.contains("验证码") || message.contains("code")) {
                        handleCodeError();
                    }
                    
                    view.onLoginError(message);
                }
            }
        });
    }
    
    @Override
    public void register(String phoneNumber, String verificationCode, String nickname) {
        if (view == null) return;
        
        // Check if currently locked out
        if (isLockedOut()) {
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            view.onRegisterError("验证码错误次数过多，请" + remainingTime + "秒后重试");
            return;
        }
        
        view.showLoading();
        
        repository.register(phoneNumber, verificationCode, nickname, new BaseRepository.RepositoryCallback<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse response) {
                if (view != null) {
                    view.hideLoading();
                    
                    // Reset error attempts on successful registration
                    resetCodeErrorAttempts();
                    
                    // Save login information
                    saveLoginInfo(response.getUser(), response.getToken(), response.getExpiresAt());
                    
                    view.onRegisterSuccess(response.getUser());
                }
            }
            
            @Override
            public void onError(String message) {
                if (view != null) {
                    view.hideLoading();
                    
                    // Handle verification code errors
                    if (message.contains("验证码") || message.contains("code")) {
                        handleCodeError();
                    }
                    
                    view.onRegisterError(message);
                }
            }
        });
    }
    
    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    @Override
    public boolean validateVerificationCode(String code) {
        return !TextUtils.isEmpty(code) && CODE_PATTERN.matcher(code).matches();
    }
    
    @Override
    public boolean validateNickname(String nickname) {
        return !TextUtils.isEmpty(nickname) && NICKNAME_PATTERN.matcher(nickname).matches();
    }
    
    /**
     * Save login information to local storage
     * @param response Login response
     */
    private void saveLoginInfo(LoginResponse response) {
        saveLoginInfo(response.getUser(), response.getToken(), response.getExpiresAt());
    }
    
    /**
     * Save login information to local storage
     * @param user User object
     * @param token Authentication token
     * @param expiresAt Token expiration time
     */
    private void saveLoginInfo(com.gameplatform.model.User user, String token, long expiresAt) {
        preferenceManager.setLoggedIn(true);
        preferenceManager.setUserId(user.getUserId());
        preferenceManager.setPhoneNumber(user.getPhoneNumber());
        preferenceManager.setNickname(user.getNickname());
        preferenceManager.setAvatar(user.getAvatar());
        preferenceManager.setAuthToken(token);
        preferenceManager.setTokenExpiresAt(expiresAt);
        preferenceManager.setLoginTime(System.currentTimeMillis());
    }
    
    /**
     * Handle verification code error
     */
    private void handleCodeError() {
        codeErrorAttempts++;
        if (codeErrorAttempts >= MAX_CODE_ATTEMPTS) {
            lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION;
        }
    }
    
    /**
     * Reset verification code error attempts
     */
    private void resetCodeErrorAttempts() {
        codeErrorAttempts = 0;
        lockoutEndTime = 0;
    }
    
    /**
     * Check if currently locked out due to too many verification code errors
     * @return true if locked out, false otherwise
     */
    private boolean isLockedOut() {
        return lockoutEndTime > System.currentTimeMillis();
    }
    

}