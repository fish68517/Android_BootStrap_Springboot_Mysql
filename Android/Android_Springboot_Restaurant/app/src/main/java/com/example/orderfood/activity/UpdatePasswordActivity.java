package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    private TextInputLayout layoutOldPassword;
    private TextInputLayout layoutNewPassword;
    private TextInputLayout layoutConfirmPassword;
    private TextInputEditText etOldPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnUpdate;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("修改密码");
    }

    private void initViews() {
        layoutOldPassword = findViewById(R.id.layout_old_password);
        layoutNewPassword = findViewById(R.id.layout_new_password);
        layoutConfirmPassword = findViewById(R.id.layout_confirm_password);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnUpdate = findViewById(R.id.btn_update);
        apiService = RetrofitClient.getInstance().getApiService();

        // 显示当前用户名
        TextView tvUsername = findViewById(R.id.tv_username);
        tvUsername.setText("当前用户: " + MyApplication.getCurUser().getNickname());

        btnUpdate.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (oldPassword.isEmpty()) {
            layoutOldPassword.setError("请输入原密码");
            return;
        }
        if (newPassword.isEmpty()) {
            layoutNewPassword.setError("请输入新密码");
            return;
        }
        if (confirmPassword.isEmpty()) {
            layoutConfirmPassword.setError("请确认新密码");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("两次输入的密码不一致");
            return;
        }

        // 清除错误提示
        layoutOldPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);

        // 更新密码
        User user = MyApplication.getCurUser();
        user.setPassword(newPassword);

        apiService.updateUserInfo(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(UpdatePasswordActivity.this, 
                        "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, 
                        "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(UpdatePasswordActivity.this, 
                    "网络错误，请重试", Toast.LENGTH_SHORT).show();
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