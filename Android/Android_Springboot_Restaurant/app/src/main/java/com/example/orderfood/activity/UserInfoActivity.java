package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class UserInfoActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private EditText etNickname;
    private EditText etPhone;
    private RadioGroup rgGender;
    private MaterialButton btnSave;
    private ApiService apiService;
    private View accountSecurityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setupToolbar();
        initViews();
        loadUserInfo();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("个人信息");
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        accountSecurityLayout = findViewById(R.id.ll_account_security);
        etNickname = findViewById(R.id.et_nickname);
        etPhone = findViewById(R.id.et_phone);
        rgGender = findViewById(R.id.rg_gender);
        btnSave = findViewById(R.id.btn_save);
        apiService = RetrofitClient.getInstance().getApiService();

        // 头像点击事件
        ivAvatar.setOnClickListener(v -> {
            // TODO: 实现头像选择功能
           //  Toast.makeText(this, "头像更换功能开发中", Toast.LENGTH_SHORT).show();
        });


        // 账号与安全点击事件
        accountSecurityLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, AccountSecurityActivity.class);
            startActivity(intent);
        });


        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void loadUserInfo() {
        User user = MyApplication.getCurUser();
        if (user != null) {
            etNickname.setText(user.getNickname());
            etPhone.setText(user.getPhone());
            
            // 设置性别
            if ("男".equals(user.getGener())) {
                rgGender.check(R.id.rb_male);
            } else if ("女".equals(user.getGener())) {
                rgGender.check(R.id.rb_female);
            }
        }
    }

    private void saveUserInfo() {
        String nickname = etNickname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gender = ((RadioButton)findViewById(rgGender.getCheckedRadioButtonId()))
                .getText().toString();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = MyApplication.getCurUser();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setGener(gender);

        apiService.updateUserInfo(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(UserInfoActivity.this, 
                        "保存成功", Toast.LENGTH_SHORT).show();
                    MyApplication.saveUser(user);
                    finish();
                } else {
                    Toast.makeText(UserInfoActivity.this, 
                        "保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, 
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