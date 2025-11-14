package com.gameplatform.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.gameplatform.R;
import com.gameplatform.base.BaseFragment;
import com.gameplatform.contract.ProfileContract;
import com.gameplatform.model.User;
import com.gameplatform.presenter.ProfilePresenter;
import com.gameplatform.ui.auth.AuthActivity;
import com.gameplatform.ui.favorites.FavoriteFragment;
import com.gameplatform.util.TimeUtil;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Profile Fragment - User profile and settings
 * Requirements: 6.1, 6.4, 6.5
 */
public class ProfileFragment extends BaseFragment implements ProfileContract.View {

    private static final String TAG = "ProfileFragment";
    
    // Views
    private CircleImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvPhone;
    private TextView tvRegisterTime;
    private LinearLayout llNickname;
    private LinearLayout llFavorites;
    private LinearLayout llComments;
    private LinearLayout llSettings;
    private LinearLayout llAbout;
    private MaterialButton btnLogout;
    
    // Presenter
    private ProfilePresenter presenter;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    protected void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvRegisterTime = view.findViewById(R.id.tv_register_time);
        llNickname = view.findViewById(R.id.ll_nickname);
        llFavorites = view.findViewById(R.id.ll_favorites);
        llComments = view.findViewById(R.id.ll_comments);
        llSettings = view.findViewById(R.id.ll_settings);
        llAbout = view.findViewById(R.id.ll_about);
        btnLogout = view.findViewById(R.id.btn_logout);
        
        setupClickListeners();
    }

    @Override
    protected void initData() {
        presenter = new ProfilePresenter(this);
        presenter.loadUserProfile();
    }

    private void setupClickListeners() {
        ivAvatar.setOnClickListener(v -> presenter.onAvatarClick());
        llNickname.setOnClickListener(v -> presenter.onNicknameClick());
        llFavorites.setOnClickListener(v -> presenter.onFavoritesClick());
        llComments.setOnClickListener(v -> presenter.onCommentsClick());
        llSettings.setOnClickListener(v -> presenter.onSettingsClick());
        llAbout.setOnClickListener(v -> presenter.onAboutClick());
        btnLogout.setOnClickListener(v -> presenter.onLogoutClick());
    }

    @Override
    public void showUserProfile(User user) {
        if (user == null) return;
        
        // Set nickname
        tvNickname.setText(user.getNickname());
        
        // Format and set phone number (hide middle digits)
        String phone = user.getPhoneNumber();
        if (phone != null && phone.length() == 11) {
            String formattedPhone = getString(R.string.phone_format, 
                phone.substring(0, 3), phone.substring(7));
            tvPhone.setText(formattedPhone);
        } else {
            tvPhone.setText(phone);
        }
        
        // Format and set register time
        String registerTime = TimeUtil.formatDate(user.getRegisterTime());
        tvRegisterTime.setText(getString(R.string.register_time_format, registerTime));
        
        // Load avatar
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                .load(user.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    @Override
    public void onProfileUpdated(User user) {
        if (user != null) {
            showUserProfile(user);
            showMessage(getString(R.string.nickname_update_success));
        } else {
            showError(getString(R.string.nickname_update_failed));
        }
    }

    @Override
    public void onAvatarUpdated(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(ivAvatar);
            showMessage(getString(R.string.avatar_update_success));
        } else {
            showError(getString(R.string.avatar_update_failed));
        }
    }

    @Override
    public void showNicknameEditDialog(String currentNickname) {
        if (getContext() == null) return;
        
        View dialogView = LayoutInflater.from(getContext())
            .inflate(R.layout.dialog_edit_nickname, null);
        
        EditText etNickname = dialogView.findViewById(R.id.et_nickname);
        etNickname.setText(currentNickname);
        etNickname.setSelection(currentNickname.length());
        
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle(R.string.edit_nickname_title)
            .setView(dialogView)
            .setPositiveButton(R.string.confirm, null) // Set to null initially
            .setNegativeButton(R.string.cancel, null)
            .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newNickname = etNickname.getText().toString().trim();
                if (presenter.validateNickname(newNickname)) {
                    presenter.updateNickname(newNickname);
                    dialog.dismiss();
                }
                // Don't dismiss dialog if validation fails
            });
        });
        
        dialog.show();
        
        // Focus on input and show keyboard
        etNickname.requestFocus();
    }

    @Override
    public void showAvatarSelectionDialog() {
        String[] options = {
            getString(R.string.camera),
            getString(R.string.gallery)
        };
        
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.select_avatar)
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // Camera - would implement camera functionality
                        showMessage("相机功能开发中");
                        break;
                    case 1:
                        // Gallery - would implement gallery functionality
                        showMessage("相册功能开发中");
                        break;
                }
            })
            .show();
    }

    @Override
    public void showLogoutConfirmation() {
        if (getContext() == null) return;
        
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                dialog.dismiss();
                presenter.confirmLogout();
            })
            .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    @Override
    public void navigateToLogin() {
        if (getActivity() == null) return;
        
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("logout", true); // Indicate this is from logout
        
        try {
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            // Handle any potential issues with navigation
            showError("跳转到登录页面失败");
        }
    }

    @Override
    public void navigateToFavorites() {
        // Switch to favorites tab in MainActivity
        Bundle data = new Bundle();
        data.putInt("tab_index", 1); // Favorites tab index
        sendInteraction("switch_tab", data);
    }

    @Override
    public void navigateToSettings() {
        showMessage("设置功能开发中");
    }

    @Override
    public void navigateToAbout() {
        showMessage("关于我们功能开发中");
    }

    @Override
    public void showLoading(boolean show) {
        super.showLoading(show);
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void showError(String error) {
        super.showError(error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}