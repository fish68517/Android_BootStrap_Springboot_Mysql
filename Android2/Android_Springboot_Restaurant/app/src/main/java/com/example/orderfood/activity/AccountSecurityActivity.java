package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.User;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSecurityActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("账号与安全");
    }

    private void initViews() {
        apiService = RetrofitClient.getInstance().getApiService();

        // 注销账号按钮点击事件
        findViewById(R.id.btn_delete_account).setOnClickListener(v -> {
            showDeleteAccountDialog();
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
            .setTitle("注销账号")
            .setMessage("确定要注销账号吗？注销后数据将无法恢复！")
            .setPositiveButton("确定", (dialog, which) -> {
                deleteAccount();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void deleteAccount() {
        User user = MyApplication.getCurUser();
        if (user == null) return;

        user.setNickname("注销用户");
        apiService.updateUserInfo(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(AccountSecurityActivity.this, 
                        "账号已注销", Toast.LENGTH_SHORT).show();
                    // 将账号存在 SharedPreferences 中清除
                    MyApplication.clearCurUser();
                    
                    // 清除本地用户数据
                    
                    // 跳转到登录页面
                    Intent intent = new Intent(AccountSecurityActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // 跳转到登录页面
                    Intent intent = new Intent(AccountSecurityActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    MyApplication.clearCurUser();
                    Toast.makeText(AccountSecurityActivity.this,
                            "账号已注销", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // 跳转到登录页面
                Intent intent = new Intent(AccountSecurityActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                MyApplication.clearCurUser();
                Toast.makeText(AccountSecurityActivity.this,
                        "账号已注销", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 