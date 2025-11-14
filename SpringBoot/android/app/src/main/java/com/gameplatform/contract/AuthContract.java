package com.gameplatform.contract;

import com.gameplatform.base.BaseView;
import com.gameplatform.model.User;

/**
 * Contract interface for Authentication module
 * Requirements: 1.1, 1.2, 2.1, 2.2
 */
public interface AuthContract {
    
    interface View extends BaseView {
        /**
         * Show verification code sent successfully
         */
        void onVerificationCodeSent();
        
        /**
         * Show login success
         * @param user Logged in user
         */
        void onLoginSuccess(User user);
        
        /**
         * Show registration success
         * @param user Registered user
         */
        void onRegisterSuccess(User user);
        
        /**
         * Show verification code error
         * @param message Error message
         */
        void onVerificationCodeError(String message);
        
        /**
         * Show login error
         * @param message Error message
         */
        void onLoginError(String message);
        
        /**
         * Show registration error
         * @param message Error message
         */
        void onRegisterError(String message);
        
        /**
         * Start countdown for verification code
         * @param seconds Countdown seconds
         */
        void startVerificationCodeCountdown(int seconds);
        
        /**
         * Navigate to main screen
         */
        void navigateToMain();
    }
    
    interface Presenter {
        /**
         * Send verification code
         * @param phoneNumber Phone number
         * @param type Type: "login" or "register"
         */
        void sendVerificationCode(String phoneNumber, String type);
        
        /**
         * Login user
         * @param phoneNumber Phone number
         * @param verificationCode Verification code
         */
        void login(String phoneNumber, String verificationCode);
        
        /**
         * Register user
         * @param phoneNumber Phone number
         * @param verificationCode Verification code
         * @param nickname User nickname
         */
        void register(String phoneNumber, String verificationCode, String nickname);
        
        /**
         * Validate phone number format
         * @param phoneNumber Phone number to validate
         * @return true if valid, false otherwise
         */
        boolean validatePhoneNumber(String phoneNumber);
        
        /**
         * Validate verification code format
         * @param code Verification code to validate
         * @return true if valid, false otherwise
         */
        boolean validateVerificationCode(String code);
        
        /**
         * Validate nickname format
         * @param nickname Nickname to validate
         * @return true if valid, false otherwise
         */
        boolean validateNickname(String nickname);
    }
}