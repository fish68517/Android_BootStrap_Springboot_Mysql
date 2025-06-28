package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.Review;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_ORDER_NO = "extra_order_no";

    private TextView tvOrderInfo, tvComment, tvReviewDate;
    private RatingBar ratingBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        // 获取 Toolbar 实例
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取返回按钮实例
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理返回按钮点击事件
                Toast.makeText(ReviewDetailActivity.this, "返回", Toast.LENGTH_SHORT).show();
                finish(); // 关闭当前 Activity
            }
        });

        tvOrderInfo = findViewById(R.id.tv_order_info);
        tvComment = findViewById(R.id.tv_comment);
        tvReviewDate = findViewById(R.id.tv_review_date);
        ratingBar = findViewById(R.id.rating_bar_detail);

        apiService = RetrofitClient.getInstance().getApiService();

        int orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        String orderNo = getIntent().getStringExtra(EXTRA_ORDER_NO);
        tvOrderInfo.setText("订单号: " + orderNo);

        if (orderId != -1) {
            loadReviewDetails(orderId);
        } else {
            Toast.makeText(this, "无效的订单ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadReviewDetails(int orderId) {
        apiService.getReviewByOrderId(orderId).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Review review = response.body();
                    ratingBar.setRating(review.getRating());
                    tvComment.setText(review.getComment());
                    if (review.getCreatedAt() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        tvReviewDate.setText("评价于: " + sdf.format(review.getCreatedAt()));
                    }
                } else {
                    Toast.makeText(ReviewDetailActivity.this, "加载评价失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ReviewDetailActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 