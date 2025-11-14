package com.gameplatform.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gameplatform.R;
import com.gameplatform.ui.auth.AuthActivity;
import com.gameplatform.base.BaseFragment;
import com.gameplatform.ui.favorites.FavoriteFragment;
import com.gameplatform.ui.games.GameListFragment;
import com.gameplatform.ui.profile.ProfileFragment;
import com.gameplatform.util.AnimationUtil;
import com.gameplatform.util.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * 主界面Activity，包含底部导航和Fragment切换
 */
public class MainActivity extends AppCompatActivity 
        implements BottomNavigationView.OnNavigationItemSelectedListener, 
                   BaseFragment.FragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private static final String CURRENT_FRAGMENT_TAG = "current_fragment";

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    // Fragment实例
    private GameListFragment gameListFragment;
    private FavoriteFragment favoriteFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 检查登录状态
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        if (!preferenceManager.isLoggedIn()) {
            Intent intent = new Intent(this, AuthActivity.class);
            if (getIntent().getBooleanExtra("logout", false)) {
                intent.putExtra("logout", true);
            }
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        initViews();
        initFragments();
        
        // 恢复Fragment状态或显示默认Fragment
        if (savedInstanceState != null) {
            restoreFragmentState(savedInstanceState);
        } else {
            showGameListFragment();
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
    }

    private void initFragments() {
        gameListFragment = new GameListFragment();
        favoriteFragment = new FavoriteFragment();
        profileFragment = new ProfileFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_game_list) {
            showGameListFragment();
            return true;
        } else if (itemId == R.id.nav_favorites) {
            showFavoriteFragment();
            return true;
        } else if (itemId == R.id.nav_profile) {
            showProfileFragment();
            return true;
        }
        
        return false;
    }

    private void showGameListFragment() {
        switchFragment(gameListFragment, "GameListFragment");
    }

    private void showFavoriteFragment() {
        switchFragment(favoriteFragment, "FavoriteFragment");
    }

    private void showProfileFragment() {
        switchFragment(profileFragment, "ProfileFragment");
    }

    /**
     * 切换Fragment with animations
     */
    private void switchFragment(Fragment fragment, String tag) {
        if (currentFragment == fragment) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // Add Material Motion transitions
        transaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        );
        
        // 隐藏当前Fragment
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // 显示目标Fragment
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, tag);
        }

        transaction.commitAllowingStateLoss();
        currentFragment = fragment;
        
        // Animate bottom navigation selection
        animateBottomNavigationSelection();
    }
    
    /**
     * Animate bottom navigation selection
     */
    private void animateBottomNavigationSelection() {
        if (bottomNavigation != null) {
            AnimationUtil.bounceClick(bottomNavigation);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        
        // 保存当前Fragment标识
        if (currentFragment != null) {
            String tag = getCurrentFragmentTag();
            if (tag != null) {
                outState.putString(CURRENT_FRAGMENT_TAG, tag);
            }
        }
    }

    private void restoreFragmentState(Bundle savedInstanceState) {
        String fragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
        
        // 恢复Fragment实例
        gameListFragment = (GameListFragment) fragmentManager.findFragmentByTag("GameListFragment");
        favoriteFragment = (FavoriteFragment) fragmentManager.findFragmentByTag("FavoriteFragment");
        profileFragment = (ProfileFragment) fragmentManager.findFragmentByTag("ProfileFragment");
        
        // 重新创建未找到的Fragment
        if (gameListFragment == null) gameListFragment = new GameListFragment();
        if (favoriteFragment == null) favoriteFragment = new FavoriteFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();
        
        // 恢复当前显示的Fragment
        if ("GameListFragment".equals(fragmentTag)) {
            currentFragment = gameListFragment;
            bottomNavigation.setSelectedItemId(R.id.nav_game_list);
        } else if ("FavoriteFragment".equals(fragmentTag)) {
            currentFragment = favoriteFragment;
            bottomNavigation.setSelectedItemId(R.id.nav_favorites);
        } else if ("ProfileFragment".equals(fragmentTag)) {
            currentFragment = profileFragment;
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        } else {
            showGameListFragment();
        }
    }

    private String getCurrentFragmentTag() {
        if (currentFragment == gameListFragment) {
            return "GameListFragment";
        } else if (currentFragment == favoriteFragment) {
            return "FavoriteFragment";
        } else if (currentFragment == profileFragment) {
            return "ProfileFragment";
        }
        return null;
    }

    // FragmentInteractionListener 接口实现
    @Override
    public void onFragmentInteraction(String action, Bundle data) {
        // 处理Fragment与Activity的交互
        if ("navigate_to_tab".equals(action) && data != null) {
            String targetTab = data.getString("target_tab");
            if ("game_list".equals(targetTab)) {
                bottomNavigation.setSelectedItemId(R.id.nav_game_list);
                showGameListFragment();
            }
        } else if ("switch_tab".equals(action) && data != null) {
            int tabIndex = data.getInt("tab_index", 0);
            switch (tabIndex) {
                case 0:
                    bottomNavigation.setSelectedItemId(R.id.nav_game_list);
                    showGameListFragment();
                    break;
                case 1:
                    bottomNavigation.setSelectedItemId(R.id.nav_favorites);
                    showFavoriteFragment();
                    break;
                case 2:
                    bottomNavigation.setSelectedItemId(R.id.nav_profile);
                    showProfileFragment();
                    break;
            }
        }
    }

    @Override
    public void showLoading(boolean show) {
        // 显示或隐藏加载状态
        // 可以在这里实现全局的加载指示器
    }

    @Override
    public void showMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Snackbar.make(findViewById(R.id.fragment_container), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(String error) {
        if (error != null && !error.isEmpty()) {
            Snackbar.make(findViewById(R.id.fragment_container), error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomNavigation = null;
        fragmentManager = null;
        currentFragment = null;
        gameListFragment = null;
        favoriteFragment = null;
        profileFragment = null;
    }
}