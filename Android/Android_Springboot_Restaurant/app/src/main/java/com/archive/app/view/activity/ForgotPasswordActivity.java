package com.archive.app.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.entity.User;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etPhone, etCode, etNewPassword, etConfirmNewPassword;
    private TextView tvGetCode;
    private Button btnResetPassword;

    private CountDownTimer countDownTimer;

    // --- 新增代码 ---
    // 用于存储我们生成的验证码，以便后续校验
    private String mVerificationCode;
    // 定义发送短信权限的请求码
    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    private String username;
    // --- 新增代码结束 ---

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

        username = getIntent().getStringExtra("username");

        // 获取当前用户

        // 设置点击事件
        ivBack.setOnClickListener(v -> finish());
        tvGetCode.setOnClickListener(v -> prepareAndSendVerificationCode());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }


    /**
     * 修改：准备并发送验证码，包含权限检查
     */
    private void prepareAndSendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查应用是否已被授予发送短信的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        } else {
            // 如果已有权限，直接发送短信
            sendSms(phone);
        }
    }

    /**
     * 新增：发送短信的实现
     * @param phone 目标手机号
     */
    private void sendSms(String phone) {
        // 1. 生成一个6位数的随机验证码
        mVerificationCode = String.format("%06d", new Random().nextInt(999999));

        // 2. 自定义您的短信内容
        String message = "【您的乐游无限】验证码是：" + mVerificationCode + "。该验证码5分钟内有效，请勿泄露于他人。";

        try {
            // 3. 获取SmsManager实例并发送短信
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(this, "验证码已发送至 " + phone, Toast.LENGTH_SHORT).show();

            // 4. 开始倒计时
            startCountdown();
        } catch (Exception e) {
            Toast.makeText(this, "验证码发送失败，请检查权限或联系客服", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 新增：将倒计时逻辑独立出来，方便复用
     */
    private void startCountdown() {
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
     * 修改：重置密码，增加了验证码校验逻辑
     */
    private void resetPassword() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // 基础验证
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "所有字段均为必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 新增：验证码校对 ---
        if (mVerificationCode == null || !mVerificationCode.equals(code)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- 校验结束 ---

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 您的后端 API 调用（占位符）---
        // (在真实场景中，验证码应该由后端发送和校验，前端仅做UI交互)
        // (这里因为是前端发送，所以前端校验后，再通知后端更新密码)

        RetrofitClient.getMainApiService().updateProfilePassword(username, newPassword).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    // 更新成功
                    Toast.makeText(ForgotPasswordActivity.this, "密码重置成功！", Toast.LENGTH_SHORT).show();
                    finish(); // 跳转到登录页面
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "密码重置失败！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }


    /**
     * 新增：处理权限请求的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，再次尝试发送短信
                String phone = etPhone.getText().toString().trim();
                sendSms(phone);
            } else {
                // 用户拒绝了权限
                Toast.makeText(this, "您拒绝了发送短信的权限，无法获取验证码", Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 防止内存泄漏
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}