package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.adapter.ProfileMenuAdapter;
import com.archive.app.model.ProfileMenuItem;
import com.archive.app.model.entity.User;
import com.archive.app.model.response.ApiResponse;
import com.archive.app.view.activity.ProfileActivity;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements ProfileMenuAdapter.OnMenuItemClickListener {

    private ImageView ivAvatar;
    private TextView tvUsername, tvNickName;
    private RecyclerView rvProfileMenu;

    private ApiService apiService;
    private MyApplication application;
    private ProfileMenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvNickName = view.findViewById(R.id.tv_email);
        rvProfileMenu = view.findViewById(R.id.rv_profile_menu);

        apiService = RetrofitClient.getMainApiService(); //

        setupMenu();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次返回页面时检查登录状态并加载
        loadUserProfile();
    }

    private void loadUserProfile() {
        User user = application.getUser();
        if (user != null) {
            tvUsername.setText(user.getNickname() != null ? user.getNickname() : user.getUsername());
            tvNickName.setText(user.getNickname());

            // 假设用户有头像URL，这里用占位符
            // ImageLoaderUtil.loadImage(getContext(), user.getAvatarUrl(), ivAvatar);
        }

        // 也可以选择从API刷新数据
/*        apiService.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    application.setUser(response.body()); // 更新本地存储
                    User freshUser = response.body();
                    tvUsername.setText(freshUser.getNickname() != null ? freshUser.getNickname() : freshUser.getUsername());
                    tvNickName.setText(freshUser.getUsername());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 刷新失败，使用本地数据
            }
        });*/
    }

    private void setupMenu() {
        rvProfileMenu.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ProfileMenuItem> menuItems = new ArrayList<>();
        // TODO: 替换为您自己的图标资源
        menuItems.add(new ProfileMenuItem("我的信息", R.drawable.ic_launcher_background));
        menuItems.add(new ProfileMenuItem("我的收藏", R.drawable.ic_launcher_background));
      // menuItems.add(new ProfileMenuItem("设置", R.drawable.ic_launcher_background));
        menuItems.add(new ProfileMenuItem("登出", R.drawable.ic_launcher_background));

        menuAdapter = new ProfileMenuAdapter(getContext(), menuItems, this);
        rvProfileMenu.setAdapter(menuAdapter);
    }

    @Override
    public void onMenuItemClick(ProfileMenuItem item) {
        String title = item.getTitle();
        if ("登出".equals(title)) {
            handleLogout();
        } else if ("我的收藏".equals(title)) {
            // TODO: 跳转到收藏页 切换到收藏Fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MyFavouriteFragment())
                    .commit();

        } else if ("我的信息".equals(title)) {
            // 弹框
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);


        } else if ("设置".equals(title)) {
            // TODO: 跳转到设置页
            Toast.makeText(getContext(), "跳转到设置", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogout() {
        apiService.logout().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // 无论后端成功与否，客户端都执行登出
                performLogout();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // 网络错误也执行登出
                performLogout();
            }
        });
    }

    private void performLogout() {
        application.logout(); // 这将清除 session 并跳转到 LoginActivity
        getActivity().finish();
    }
}