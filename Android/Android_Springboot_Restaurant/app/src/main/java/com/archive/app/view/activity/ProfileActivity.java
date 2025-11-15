package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.entity.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ApiService apiService = RetrofitClient.getMainApiService();
    private User user;

    /**
     * 4. 处理 Toolbar 上所有按钮的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理返回按钮的点击事件
        if (item.getItemId() == android.R.id.home) {
            finish(); // 关闭当前 Activity，返回上一个页面
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // 1. 找到 Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // 2. 将 Toolbar 设置为 SupportActionBar
        setSupportActionBar(toolbar);

        // 3. 启用返回按钮 (箭头)
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        TextInputEditText etNickname = findViewById(R.id.et_nickname);
        TextInputEditText password = findViewById(R.id.et_new_password);




        MaterialButton btnSaveChanges = findViewById(R.id.btn_save_changes);
        MaterialButton deleteButton = findViewById(R.id.btn_delete_account);


        user = MyApplication.getUser();

        etNickname.setText(user.getNickname());
        password.setText(user.getPasswordHash());

        // 设置数据和点击事件
        // e.g., tvUserId.setText("13");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("删除账户")
                        .setMessage("确定要删除账户吗？")
                        .setPositiveButton("确定", (dialog1, which) -> {
                            // 删除账户逻辑
                            apiService.deleteAccount(user.getUserId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "账户已删除", Toast.LENGTH_SHORT).show();
                                        finish();
                                        // 跳转到登录页面
                                        startActivity(new Intent(ProfileActivity.this, SplashActivity.class));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
            }
        });

        btnSaveChanges.setOnClickListener(v -> {
            // 处理保存逻辑
            user.setNickname(etNickname.getText().toString());
            user.setPasswordHash(password.getText().toString());
            apiService.updateProfile(user.getUserId(),etNickname.getText().toString(), password.getText().toString())
                    .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "用户信息保存成功", Toast.LENGTH_SHORT).show();
                        MyApplication.setUser(response.body());
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        });
    }
}