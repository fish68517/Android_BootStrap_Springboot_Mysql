package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 获取按钮和图片的引用
        Button loginButton = findViewById(R.id.loginButton);
        ImageView arrowImageView = findViewById(R.id.arrowImageView);

        // 为登录按钮设置点击事件监听器
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理登录逻辑
                Toast.makeText(SplashActivity.this, "登录按钮被点击", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 为箭头图标（注册入口）设置点击事件监听器
        arrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理跳转到注册页面的逻辑
                Toast.makeText(SplashActivity.this, "注册账号被点击", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}