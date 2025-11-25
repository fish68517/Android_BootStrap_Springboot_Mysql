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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.dto.UserRegisterDTO;
import com.archive.app.model.response.ApiResponse;

import java.util.Random;
import java.util.regex.Pattern;

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

    // --- 新增代码：用于短信验证 ---
    private EditText  etCode;
    private TextView tvGetCode;
    private CountDownTimer countDownTimer;
    private String mVerificationCode; // 用于存储生成的验证码
    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    // --- 新增代码结束 ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getMainApiService();

        // 绑定视图
        ivBack = findViewById(R.id.iv_back);
        // etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole = findViewById(R.id.rg_role);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);


        etUsername = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        tvGetCode = findViewById(R.id.tv_get_code);



        // 设置点击事件
        ivBack.setOnClickListener(v -> finish());
        tvLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> registerUser());

        // --- 新增代码：设置获取验证码的点击事件 ---
        tvGetCode.setOnClickListener(v -> prepareAndSendVerificationCode());
        // --- 新增代码结束 ---
    }


    /**
     * 新增：验证手机号格式是否正确
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入11位有效手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 中国大陆手机号常用正则表达式
        String regex = "^1[3-9]\\d{9}$";
        if (!Pattern.matches(regex, phone)) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 新增：准备并发送验证码，包含权限和格式检查
     */
    private void prepareAndSendVerificationCode() {
        String phone = etUsername.getText().toString().trim();
        if (!isValidPhoneNumber(phone)) {
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
        String message = "【您的乐游无限】您的注册验证码是：" + mVerificationCode + "。该验证码5分钟内有效，请勿泄露于他人。";

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
     * 新增：将倒计时逻辑独立出来
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


    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        // --- 新增：获取手机号和验证码 ---
        String phone = etUsername.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) { // 新增验证
            Toast.makeText(this, "所有字段均为必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 新增：验证码校对 ---
        if (mVerificationCode == null || !mVerificationCode.equals(code)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- 校验结束 ---

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取角色
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRoleId);
        // "user" 或 "publisher"
        String role = "user";
      /*  if (selectedRadioButton.getId() == R.id.rb_publisher) {
            role = "publisher";
        }*/

        // 创建 DTO (假设您的DTO也需要phone字段)
        UserRegisterDTO registerDTO = new UserRegisterDTO(username, password, confirmPassword, role);
        // 如果需要，可以修改DTO以包含phone, 例如: new UserRegisterDTO(username, password, role, phone);

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
                    String errorMsg = "注册失败，请稍后重试";
                    if(response.body() != null && !TextUtils.isEmpty(response.body().getMessage())) {
                        errorMsg = response.body().getMessage(); // 使用后端返回的错误信息
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // 网络错误
                Toast.makeText(RegisterActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                String phone = etUsername.getText().toString().trim();
                if (isValidPhoneNumber(phone)) { // 再次校验
                    sendSms(phone);
                }
            } else {
                // 用户拒绝了权限
                Toast.makeText(this, "您拒绝了发送短信的权限，无法完成注册", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 新增：在Activity销毁时取消计时器，防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}