package com.gameplatform.contract;

import com.gameplatform.base.BaseView;
import com.gameplatform.model.User;

/**
 * Contract interface for Profile module
 * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5
 */
public interface ProfileContract {
    
    interface View extends BaseView {
        /**
         * Show user profile
         * @param user User profile information
         */
        void showUserProfile(User user);
        
        /**
         * Show profile updated successfully
         * @param user Updated user profile
         */
        void onProfileUpdated(User user);
        
        /**
         * Show avatar updated successfully
         * @param avatarUrl New avatar URL
         */
        void onAvatarUpdated(String avatarUrl);
        
        /**
         * Show nickname edit dialog
         * @param currentNickname Current nickname
         */
        void showNicknameEditDialog(String currentNickname);
        
        /**
         * Show avatar selection dialog
         */
        void showAvatarSelectionDialog();
        
        /**
         * Show logout confirmation dialog
         */
        void showLogoutConfirmation();
        
        /**
         * Navigate to login screen
         */
        void navigateToLogin();
        
        /**
         * Navigate to favorites
         */
        void navigateToFavorites();
        
        /**
         * Navigate to settings
         */
        void navigateToSettings();
        
        /**
         * Navigate to about
         */
        void navigateToAbout();
    }
    
    interface Presenter {
        /**
         * Load user profile
         */
        void loadUserProfile();
        
        /**
         * Handle avatar click
         */
        void onAvatarClick();
        
        /**
         * Handle nickname click
         */
        void onNicknameClick();
        
        /**
         * Update nickname
         * @param newNickname New nickname
         */
        void updateNickname(String newNickname);
        
        /**
         * Update avatar
         * @param avatarPath Avatar file path
         */
        void updateAvatar(String avatarPath);
        
        /**
         * Handle favorites click
         */
        void onFavoritesClick();
        
        /**
         * Handle settings click
         */
        void onSettingsClick();
        
        /**
         * Handle about click
         */
        void onAboutClick();
        
        /**
         * Handle logout click
         */
        void onLogoutClick();
        
        /**
         * Confirm logout
         */
        void confirmLogout();
        
        /**
         * Validate nickname
         * @param nickname Nickname to validate
         * @return true if valid, false otherwise
         */
        boolean validateNickname(String nickname);
    }
}