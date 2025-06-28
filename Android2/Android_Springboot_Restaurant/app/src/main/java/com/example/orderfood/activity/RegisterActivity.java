package com.example.orderfood.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.RetrofitClient;
import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.model.User;
import com.example.orderfood.view.CaptchaView;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;

    private static final String[] PREFIXES = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
            "150", "151", "152", "153", "155", "156", "157", "158", "159",
            "170", "171", "172", "173", "175", "176", "177", "178",
            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
            "191", "192", "193", "195", "196", "197", "198", "199"};
    private Button registerButton;

    private CaptchaView captchaView; // 新增验证码视图

    private EditText emailEditText;
    private EditText captchaInputEditText;

    private RadioGroup registerRadioGroup;
    private RadioButton studentRegisterRadioButton;
    private RadioButton merchantRegisterRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);

        registerRadioGroup = findViewById(R.id.register_radio_group);
        studentRegisterRadioButton = findViewById(R.id.radio_student_register);
        merchantRegisterRadioButton = findViewById(R.id.radio_merchant_register);
        registerButton = findViewById(R.id.register_button);

        registerButton = findViewById(R.id.register_button);
        // 初始化新增的视图
        captchaInputEditText = findViewById(R.id.captcha_input);
        captchaView = findViewById(R.id.captcha_view);


        registerButton.setOnClickListener(v -> {
            if (validateInput()) { // 先进行输入验证
                handleRegister();  // 验证通过再执行注册
            }
        });
    }

    /**
     * 验证所有输入，包括验证码
     * @return true 如果所有输入都有效
     */
    private boolean validateInput() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String captchaInput = captchaInputEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(captchaInput)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 核心：验证码校验（忽略大小写）
        if (!captchaInput.equalsIgnoreCase(captchaView.getCode())) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            // 刷新验证码，让用户重试
            captchaView.refresh();
            // 清空用户的输入
            captchaInputEditText.setText("");
            return false;
        }

        return true; // 所有验证通过
    }


    private void handleRegister() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        registerStudent(username, password);
    }

    public static String generatePhoneNumber() {
        Random random = new Random();
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)]; // 随机选择一个号段
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10)); // 生成 0-9 之间的随机数
        }
        return sb.toString();
    }

    private void registerStudent(String username, String password) {
        User user = new User();
        user.setNickname(username);
        user.setPassword(password);
        // 将drawable 下的图片名称赋值给 user.avatar
        user.setAvatar("ic_avatar");
        String phoneNumber = generatePhoneNumber();
        user.setPhone(phoneNumber);

        RetrofitClient.getInstance().getApiService().register(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                if (response.body() == true) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();    // 注册成功后关闭当前页面
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                System.out.println("注册失败：" + t.getMessage());
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
