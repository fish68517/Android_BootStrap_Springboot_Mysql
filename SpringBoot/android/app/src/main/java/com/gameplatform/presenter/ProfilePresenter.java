package com.gameplatform.presenter;

import android.text.TextUtils;

import com.gameplatform.base.BasePresenter;
import com.gameplatform.contract.ProfileContract;
import com.gameplatform.model.User;
import com.gameplatform.repository.ProfileRepository;
import com.gameplatform.util.PreferenceManager;

/**
 * Profile Presenter - Handle profile business logic
 * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5
 */
public class ProfilePresenter extends BasePresenter<ProfileContract.View> 
    implements ProfileContract.Presenter {

    private static final String TAG = "ProfilePresenter";
    
    private ProfileRepository repository;
    private PreferenceManager preferenceManager;

    public ProfilePresenter(ProfileContract.View view) {
        super(view);
        this.repository = new ProfileRepository();
        this.preferenceManager = PreferenceManager.getInstance();
    }

    @Override
    public void loadUserProfile() {
        if (view == null) return;
        
        view.showLoading(true);
        
        // Load user profile from local storage first
        User localUser = preferenceManager.getCurrentUser();
        if (localUser != null) {
            view.showUserProfile(localUser);
        }
        
        // Then load from server
        repository.getUserProfile(new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (view != null) {
                    view.showLoading(false);
                    view.showUserProfile(user);
                    // Update local storage
                    preferenceManager.saveCurrentUser(user);
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.showLoading(false);
                    // If server fails, show local data if available
                    if (localUser == null) {
                        view.showError(error);
                    }
                }
            }
        });
    }

    @Override
    public void onAvatarClick() {
        if (view != null) {
            view.showAvatarSelectionDialog();
        }
    }

    @Override
    public void onNicknameClick() {
        if (view != null) {
            User currentUser = preferenceManager.getCurrentUser();
            String currentNickname = currentUser != null ? currentUser.getNickname() : "";
            view.showNicknameEditDialog(currentNickname);
        }
    }

    @Override
    public void updateNickname(String newNickname) {
        if (view == null || !validateNickname(newNickname)) {
            return;
        }
        
        view.showLoading(true);
        
        repository.updateNickname(newNickname, new ProfileRepository.UpdateCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                if (view != null) {
                    view.showLoading(false);
                    view.onProfileUpdated(updatedUser);
                    // Update local storage
                    preferenceManager.saveCurrentUser(updatedUser);
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.showLoading(false);
                    view.showError(error);
                }
            }
        });
    }

    @Override
    public void updateAvatar(String avatarPath) {
        if (view == null || TextUtils.isEmpty(avatarPath)) {
            return;
        }
        
        view.showLoading(true);
        
        repository.updateAvatar(avatarPath, new ProfileRepository.AvatarCallback() {
            @Override
            public void onSuccess(String avatarUrl) {
                if (view != null) {
                    view.showLoading(false);
                    view.onAvatarUpdated(avatarUrl);
                    
                    // Update local user data
                    User currentUser = preferenceManager.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.setAvatar(avatarUrl);
                        preferenceManager.saveCurrentUser(currentUser);
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.showLoading(false);
                    view.showError(error);
                }
            }
        });
    }

    @Override
    public void onFavoritesClick() {
        if (view != null) {
            view.navigateToFavorites();
        }
    }

    @Override
    public void onCommentsClick() {
        // Navigate to user comments - not implemented in current tasks
        if (view != null) {
            view.showMessage("我的评论功能开发中");
        }
    }

    @Override
    public void onSettingsClick() {
        if (view != null) {
            view.navigateToSettings();
        }
    }

    @Override
    public void onAboutClick() {
        if (view != null) {
            view.navigateToAbout();
        }
    }

    @Override
    public void onLogoutClick() {
        if (view != null) {
            view.showLogoutConfirmation();
        }
    }

    @Override
    public void confirmLogout() {
        if (view == null) return;
        
        view.showLoading(true);
        
        // Perform logout on server (optional - could be implemented later)
        // For now, just clear local data
        performLocalLogout();
    }
    
    /**
     * Perform local logout by clearing all user data
     */
    private void performLocalLogout() {
        try {
            // Clear all user data and login state
            preferenceManager.clearUserData();
            
            // Clear any cached data (could be extended to clear image cache, etc.)
            clearCachedData();
            
            if (view != null) {
                view.showLoading(false);
                view.showMessage("已退出登录");
                
                // Navigate to login screen after a short delay
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    if (view != null) {
                        view.navigateToLogin();
                    }
                }, 1000); // 1 second delay to show the message
            }
        } catch (Exception e) {
            if (view != null) {
                view.showLoading(false);
                view.showError("退出登录失败，请重试");
            }
        }
    }
    
    /**
     * Clear cached data (can be extended for more comprehensive cleanup)
     */
    private void clearCachedData() {
        // Clear any in-memory caches
        // This could be extended to clear image cache, database cache, etc.
        // For now, just ensure all user-related data is cleared
    }

    @Override
    public boolean validateNickname(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            if (view != null) {
                view.showError("请输入昵称");
            }
            return false;
        }
        
        if (nickname.length() < 2 || nickname.length() > 20) {
            if (view != null) {
                view.showError("昵称长度应为2-20个字符");
            }
            return false;
        }
        
        // Check for invalid characters (only allow Chinese, English, numbers, and some symbols)
        if (!nickname.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_\\-\\s]+$")) {
            if (view != null) {
                view.showError("昵称只能包含中文、英文、数字、下划线和横线");
            }
            return false;
        }
        
        // Check if nickname is the same as current
        User currentUser = preferenceManager.getCurrentUser();
        if (currentUser != null && nickname.equals(currentUser.getNickname())) {
            if (view != null) {
                view.showMessage("昵称未发生变化");
            }
            return false;
        }
        
        return true;
    }
}