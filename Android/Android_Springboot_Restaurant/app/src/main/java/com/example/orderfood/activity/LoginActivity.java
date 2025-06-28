package com.example.orderfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private ImageView logoImageView;
    private CheckBox rememberMeCheckBox;  // 添加 CheckBox 的引用


    private EditText regionEditText;

    private RadioGroup loginRadioGroup;
    private RadioButton studentRadioButton;
    private RadioButton merchantRadioButton;
    private RadioButton adminLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logoImageView = findViewById(R.id.logo);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        rememberMeCheckBox = findViewById(R.id.remember_me);  // 初始化 CheckBox

        regionEditText = findViewById(R.id.region);
        loginRadioGroup = findViewById(R.id.login_radio_group);
        studentRadioButton = findViewById(R.id.radio_student);
        merchantRadioButton = findViewById(R.id.radio_merchant);
        adminLoginButton = findViewById(R.id.admin_login);


        // 检查是否保存了登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true); // 设置 CheckBox 状态
        }

        loginButton.setOnClickListener(v -> {
            handleLogin();
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loadLoginInfo();
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {  // 检查输入是否为空
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        loginStudent(username, password);
    }



    private void loginStudent(String nickname, String password) {

        RetrofitClient.getInstance().getApiService().login(nickname, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.body() != null) {
                    User user = response.body();
                    if (user.getNickname() == "注销用户") {
                        Toast.makeText(LoginActivity.this, "该用户已注销", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MyApplication.saveUser(user);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    saveLoginInfo(nickname, password);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();  // 登录成功后关闭当前页面
                } else {
                    finish();    // 注册成功后关闭当前页面
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Toast.makeText(LoginActivity.this, "该用户已注销", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void saveLoginInfo(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putBoolean("rememberMe", true);
            editor.putString("username", username);
            editor.putString("password", password);
        } else {
            editor.clear();  // 如果用户未勾选"记住我"，则清空保存的登录信息
        }
        editor.apply();
    }

    // 清理 SharedPreferences 中对应username 的登录信息

    private void clearLoginInfo(String username,String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(username);
        editor.remove("password");  // 删除 password 键值对
        editor.apply();
    }

    // 清理 SharedPreferences 中所有登录信息


    // 从 sharedPreferences 中读取登录信息
    private void loadLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true); // 设置 CheckBox 状态
        }
    }
}
