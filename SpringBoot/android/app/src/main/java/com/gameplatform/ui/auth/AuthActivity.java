package com.gameplatform.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gameplatform.R;
import com.gameplatform.contract.AuthContract;
import com.gameplatform.model.User;
import com.gameplatform.ui.main.MainActivity;
import com.gameplatform.util.AnimationUtil;

/**
 * Authentication Activity with Login and Register tabs
 * Requirements: 1.1, 1.4, 2.1, 2.4
 */
public class AuthActivity extends AppCompatActivity implements AuthContract.View {
    
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AuthPagerAdapter pagerAdapter;
    
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        initViews();
        setupViewPager();
        
        // Handle logout flag
        if (getIntent().getBooleanExtra("logout", false)) {
            // Could show a logout success message or perform additional cleanup
            // For now, just ensure we're on the login tab
            viewPager.setCurrentItem(0, false);
        }
    }
    
    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }
    
    private void setupViewPager() {
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.login);
                    break;
                case 1:
                    tab.setText(R.string.register);
                    break;
            }
        }).attach();
    }
    
    @Override
    public void showLoading() {
        // Show loading indicator
    }
    
    @Override
    public void hideLoading() {
        // Hide loading indicator
    }
    
    @Override
    public void showError(String message) {
        // Show error message using Snackbar or Toast
    }
    
    @Override
    public void onVerificationCodeSent() {
        // Handle verification code sent
    }
    
    @Override
    public void onLoginSuccess(User user) {
        navigateToMain();
    }
    
    @Override
    public void onRegisterSuccess(User user) {
        navigateToMain();
    }
    
    @Override
    public void onVerificationCodeError(String message) {
        showError(message);
    }
    
    @Override
    public void onLoginError(String message) {
        showError(message);
    }
    
    @Override
    public void onRegisterError(String message) {
        showError(message);
    }
    
    @Override
    public void startVerificationCodeCountdown(int seconds) {
        // Start countdown timer for verification code
    }
    
    @Override
    public void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * ViewPager2 adapter for Login and Register fragments
     */
    private class AuthPagerAdapter extends FragmentStateAdapter {
        
        public AuthPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return loginFragment;
                case 1:
                    return registerFragment;
                default:
                    return loginFragment;
            }
        }
        
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}