package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.OrderResponse;
import com.example.orderfood.model.User;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ApiService service = RetrofitClient.getInstance().getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        setupToolbar();
        initViews();
        User user = MyApplication.getCurUser();
        user.setPoints(10);
        service.updateUserInfo(user);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("支付成功");
    }

    private void initViews() {
        // 获取传递的订单信息
        String orderId = getIntent().getStringExtra("orderId");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        
        // 设置订单信息
        TextView textViewOrderId = findViewById(R.id.textViewOrderId);
        TextView textViewAmount = findViewById(R.id.textViewAmount);
        
        textViewOrderId.setText("订单号：" + orderId);
        textViewAmount.setText(String.format("支付金额：¥%.2f", amount));

        // 查看订单按钮
        findViewById(R.id.buttonViewOrder).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        });

        // 返回首页按钮
        findViewById(R.id.buttonBackToHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回首页
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 重写返回键行为，返回首页
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
