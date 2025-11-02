package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.R;


public class WelcomActivity extends AppCompatActivity {

    private TextView tvSkip;
    private Button btnEnter;
    private CountDownTimer countDownTimer;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        tvSkip = findViewById(R.id.tv_skip);
        title = findViewById(R.id.tv_title);

        btnEnter = findViewById(R.id.btn_enter);

        // 设置倒计时
        countDownTimer = new CountDownTimer(5000, 1000) { // 5秒倒计时，每1秒更新一次
            @Override
            public void onTick(long millisUntilFinished) {
                tvSkip.setText((millisUntilFinished / 1000) + "S");
            }

            @Override
            public void onFinish() {
                navigateToMain(); // 倒计时结束后跳转到主页
            }
        }.start();

        // 点击跳过按钮，立即跳转到主页
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel(); // 取消倒计时
                navigateToMain(); // 跳转到主页
            }
        });

        // 点击“欢迎进入”按钮，立即跳转到主页
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel(); // 取消倒计时
                navigateToMain(); // 跳转到主页
            }
        });
    }

    // 跳转到主页的方法
    private void navigateToMain() {
        Intent intent = new Intent(WelcomActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // 销毁当前欢迎页面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // 确保计时器在Activity销毁时取消
        }
    }
}
