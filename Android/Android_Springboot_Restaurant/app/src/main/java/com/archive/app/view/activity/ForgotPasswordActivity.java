package com.archive.app.view.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etPhone, etCode, etNewPassword, etConfirmNewPassword;
    private TextView tvGetCode;
    private Button btnResetPassword;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 绑定视图
        ivBack = findViewById(R.id.iv_back);
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        tvGetCode = findViewById(R.id.tv_get_code);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // 设置点击事件
        ivBack.setOnClickListener(v -> finish());
        tvGetCode.setOnClickListener(v -> getVerificationCode());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    /**
     * 【占位符】获取验证码
     * 您的第三方 API 逻辑应在此处调用
     */
    private void getVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 您的第三方 API 调用（占位符）---
        Toast.makeText(this, "正在发送验证码至 " + phone, Toast.LENGTH_SHORT).show();
        // --- 占位符结束 ---

        // 开始倒计时
        tvGetCode.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetCode.setText((millisUntilFinished / 1000) + "s 后重试");
            }

            @Override
            public void onFinish() {
                tvGetCode.setText("获取验证码");
                tvGetCode.setEnabled(true);
            }
        }.start();
    }

    /**
     * 【占位符】重置密码
     * 您的后端应提供一个“找回密码”的 API 接口
     */
    private void resetPassword() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // 验证
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "所有字段均为必填项", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 您的后端 API 调用（占位符）---
        // (您后端目前缺少此接口, 这里仅作演示)
        // apiService.resetPassword(phone, code, newPassword).enqueue(...)
        Toast.makeText(this, "密码重置成功！", Toast.LENGTH_SHORT).show();
        // --- 占位符结束 ---

        finish(); // 返回登录页
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 防止内存泄漏
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}