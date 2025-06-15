package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.DailyCouponAdapter;
import com.example.orderfood.model.Coupon;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCouponActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailyCouponAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_coupon);

        setupToolbar();
        initViews();
        loadCoupons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("每日优惠券");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DailyCouponAdapter(this::claimCoupon);
        recyclerView.setAdapter(adapter);
        apiService = RetrofitClient.getInstance().getApiService();
    }

    private void loadCoupons() {
        // 模拟优惠券数据
        List<Coupon> coupons = new ArrayList<>();
        
        Coupon coupon1 = new Coupon();
        coupon1.setCouponName("新人专享券");
        coupon1.setCouponType(1); // 满减券
        coupon1.setMinAmount(new BigDecimal("30"));
        coupon1.setDiscountAmount(new BigDecimal("10"));
        coupon1.setValidDays(7);
        coupons.add(coupon1);

        Coupon coupon2 = new Coupon();
        coupon2.setCouponName("周末特惠券");
        coupon2.setCouponType(2); // 折扣券
        coupon2.setMinAmount(new BigDecimal("50"));
        coupon2.setDiscountRate(new BigDecimal("0.8"));
        coupon2.setValidDays(3);
        coupons.add(coupon2);

        Coupon coupon3 = new Coupon();
        coupon3.setCouponName("生日福利券");
        coupon3.setCouponType(1);
        coupon3.setMinAmount(new BigDecimal("100"));
        coupon3.setDiscountAmount(new BigDecimal("30"));
        coupon3.setValidDays(30);
        coupons.add(coupon3);

        adapter.setCoupons(coupons);
    }

    private void claimCoupon(Coupon coupon) {
        // 设置用户ID
        coupon.setStatus(1); // 1表示已领取未使用
        
        apiService.postCoupon(coupon).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(DailyCouponActivity.this, 
                        "领取成功！", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DailyCouponActivity.this, 
                        "领取失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(DailyCouponActivity.this, 
                    "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
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