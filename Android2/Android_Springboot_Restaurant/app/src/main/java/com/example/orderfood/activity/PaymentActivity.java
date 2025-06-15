package com.example.orderfood.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.PaymentResponse;
import com.example.orderfood.model.PaymentStatusResponse;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private String orderId;
    private double amount;
    private int paymentMethod;

    private ApiService apiService = RetrofitClient.getInstance().getApiService();
    private Handler handler = new Handler();
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 获取传递的参数
        order = (Order) getIntent().getSerializableExtra("order");
        orderId = order.getOrderId()+"";
        amount = order.getTotalAmount();
        paymentMethod = getIntent().getIntExtra("paymentMethod", 1);

        setupToolbar();
        initViews();
        // initPayment();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("支付订单");
    }

    private void initViews() {
        TextView textViewAmount = findViewById(R.id.textViewAmount);
        textViewAmount.setText(String.format("¥%.2f", amount));
        ImageView imageViewQRCode = findViewById(R.id.imageViewQRCode);

        String qrCodeUrl; // 用一个变量来存储最终要编码的URL

        if (paymentMethod == Order.PaymentMethod.WECHAT) {
            // **模拟**的微信支付二维码URL
            qrCodeUrl = "weixin://wxpay/bizpayurl?pr=SAMPLE123456789";
            setTitle("微信支付"); // 可以顺便设置一下标题
        } else {
            // **模拟**的支付宝支付二维码URL
            qrCodeUrl = "https://qr.alipay.com/fkx99999xxxxxx"; // 使用一个符合支付宝格式的模拟URL
            setTitle("支付宝支付");
        }

        // 使用 ZXing 生成二维码 (代码优化，避免重复)
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            // 参数：内容、码的类型、宽、高
            Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    qrCodeUrl,
                    BarcodeFormat.QR_CODE,
                    800,
                    800
            );
            imageViewQRCode.setImageBitmap(bitmap);
        } catch (Exception e){
            Toast.makeText(this, "生成二维码失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void initPayment() {
        // 调用后端接口获取支付参数
        if (true) {
            PaymentResponse response = new PaymentResponse();
            response.setQrCodeUrl(amount + "RMB");
            generatePaymentQRCode(response);
            return;
        }

        apiService.getPaymentParams(orderId, paymentMethod)
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 处理支付参数，生成二维码等
                            generatePaymentQRCode(response.body());
                        } else {
                            Toast.makeText(PaymentActivity.this, 
                                    "获取支付参数失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        Toast.makeText(PaymentActivity.this, 
                                "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generatePaymentQRCode(PaymentResponse response) {
        if (response.getQrCodeUrl() != null && !response.getQrCodeUrl().isEmpty()) {
            ImageView imageViewQRCode = findViewById(R.id.imageViewQRCode);
            
            // 使用 ZXing 生成二维码
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    response.getQrCodeUrl(), 
                    BarcodeFormat.QR_CODE, 
                    800, 
                    800
                );
                imageViewQRCode.setImageBitmap(bitmap);
                
                // 开始轮询支付状态
                startCheckPaymentStatus();
            } catch (Exception e) {
                Toast.makeText(this, "生成二维码失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "获取支付二维码失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCheckPaymentStatus() {
        // 每3秒查询一次支付状态
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPaymentStatus();
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void checkPaymentStatus() {
        apiService.checkPaymentStatus(orderId)
                .enqueue(new Callback<PaymentStatusResponse>() {
                    @Override
                    public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PaymentStatusResponse status = response.body();
                            if (status.isPaid()) {
                                // 支付成功，停止轮询
                                handler.removeCallbacksAndMessages(null);
                                // 跳转到支付成功页面
                                Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("amount", amount);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // 支付成功，停止轮询
                            handler.removeCallbacksAndMessages(null);
                            // 跳转到支付成功页面
                            Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("amount", amount);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                        // 处理错误
                        // 支付成功，停止轮询
                        handler.removeCallbacksAndMessages(null);
                        // 跳转到支付成功页面
                        Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("amount", amount);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止轮询
        handler.removeCallbacksAndMessages(null);
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