package com.archive.app.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.dto.UserRegisterDTO;
import com.archive.app.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageView ivBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getMainApiService(); //

        // 绑定视图
        ivBack = findViewById(R.id.iv_back);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole = findViewById(R.id.rg_role);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // 设置点击事件
        ivBack.setOnClickListener(v -> finish());
        tvLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "所有字段均为必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取角色
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRoleId);
        // "user" 或 "publisher"
        String role = "user";
        if (selectedRadioButton.getId() == R.id.rb_publisher) {
            role = "publisher";
        }

        // 创建 DTO
        UserRegisterDTO registerDTO = new UserRegisterDTO(username, password, confirmPassword, role);

        // 调用 API
        apiService.register(registerDTO).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 注册成功
                    Toast.makeText(RegisterActivity.this, "注册成功！请登录。", Toast.LENGTH_SHORT).show();
                    finish(); // 关闭注册页，返回登录页
                } else {
                    // 注册失败（例如用户名已存在）
                    Toast.makeText(RegisterActivity.this, "注册失败，用户名可能已被占用", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // 网络错误
                Toast.makeText(RegisterActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}