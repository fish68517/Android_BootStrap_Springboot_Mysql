package com.example.orderfood.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.R;
import com.example.orderfood.view.LuckyWheelView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LuckyWheelActivity extends AppCompatActivity {

    private LuckyWheelView ivWheel;
    private View btnStart;
    private boolean isRotating = false;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LuckyWheelPrefs";
    private static final String LAST_DRAW_DATE = "lastDrawDate";

    // 奖品区域角度(360/6=60度每个区域)
    private final String[] prizes = {"满100减20券", "85折优惠券", "再来一杯咖啡", "谢谢惠顾"};
    // 每个奖品对应的角度
    private final float[] angles = {-90, 0, 90, 180}; // 从正上方开始计算
    private boolean isShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_wheel);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("每日一抽");
    }

    private void initViews() {
        ivWheel = findViewById(R.id.iv_wheel);
        btnStart = findViewById(R.id.btn_start);

        // 检查是否已经抽过奖
        if (isTodayAlreadyDrawn()) {
            btnStart.setEnabled(false);
            Toast.makeText(this, "今天已经抽过奖了，明天再来吧！", Toast.LENGTH_SHORT).show();
        } else {
            btnStart.setEnabled(true);
        }

        btnStart.setOnClickListener(v -> {
            System.out.println("ggg");
            if (!isRotating) {
                startRotation();
            }
        });
    }

    private boolean isTodayAlreadyDrawn() {
        String lastDrawDate = sharedPreferences.getString(LAST_DRAW_DATE, "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        return currentDate.equals(lastDrawDate);
    }

    private void startRotation() {
        if (isRotating) return;
        isRotating = true;
        btnStart.setEnabled(false);

        // 随机生成一个奖品索引
        int randomIndex = new Random().nextInt(angles.length);

        // 计算转盘需要旋转的角度
        float targetAngle = 360 - angles[randomIndex] - (360 / angles.length / 2);
        float targetRotation = 360 * 10 + targetAngle;

        // 使用属性动画旋转转盘
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(ivWheel, "rotation", 0, targetRotation);
        rotateAnimator.setDuration(10000); // 10秒
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        rotateAnimator.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            if (currentValue >= targetRotation - 1) {

                if (isShowDialog) return;
                // 动画结束
                isRotating = false;
                System.out.println("抽奖结束");




                // 计算中奖奖品的索引
                int prizeIndex = (int) ((360 - currentValue % 360) / (360 / angles.length));

                // 显示中奖信息
                new AlertDialog.Builder(this)
                        .setTitle("恭喜")
                        .setMessage("恭喜获得: " + prizes[prizeIndex])
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                isShowDialog = false;
                            }
                        })
                        .show();

                isShowDialog = true;
                System.out.println("抽奖结束  111");
                // 更新抽奖日期
                updateLastDrawDate();
            }
        });

        rotateAnimator.start();
    }

    private void updateLastDrawDate() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        editor.putString(LAST_DRAW_DATE, currentDate);
        editor.apply();
        btnStart.setEnabled(false);

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