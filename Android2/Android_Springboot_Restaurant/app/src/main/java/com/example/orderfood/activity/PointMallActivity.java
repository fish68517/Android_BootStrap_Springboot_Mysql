package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.PointProductAdapter;
import com.example.orderfood.model.PointExchange;
import com.example.orderfood.model.PointProduct;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointMallActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PointProductAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_mall);

        setupToolbar();
        initViews();
        loadProducts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("积分商城");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PointProductAdapter(this::exchangeProduct);
        recyclerView.setAdapter(adapter);
        apiService = RetrofitClient.getInstance().getApiService();
    }

    private void loadProducts() {
        apiService.listPointProducts().enqueue(new Callback<List<PointProduct>>() {
            @Override
            public void onResponse(Call<List<PointProduct>> call, Response<List<PointProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProducts(response.body());
                } else {
                    Toast.makeText(PointMallActivity.this, 
                        "加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PointProduct>> call, Throwable t) {
                Toast.makeText(PointMallActivity.this, 
                    "网络错误，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exchangeProduct(PointProduct product) {
        new AlertDialog.Builder(this)
            .setTitle("兑换确认")
            .setMessage(String.format("确定要使用%d积分兑换【%s】吗？", 
                product.getPointsRequired(), product.getProductName()))
            .setPositiveButton("确定", (dialog, which) -> {
                performExchange(product);
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void performExchange(PointProduct product) {
        // 判断积分是否足够
        if (MyApplication.getCurUser().getPoints() < product.getPointsRequired()) {
            Toast.makeText(PointMallActivity.this,
                "积分不足，无法兑换", Toast.LENGTH_SHORT).show();
            return;
        }
        PointExchange exchange = new PointExchange();
        exchange.setUserId(MyApplication.getCurUser().getUserId());
        exchange.setProductId(product.getProductId());
        exchange.setPointCost(product.getPointsRequired());
        exchange.setExchangeTime(new Date());
        exchange.setStatus(0); // 待发货

        apiService.savePointExchange(exchange).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PointMallActivity.this, 
                        "兑换成功！", Toast.LENGTH_SHORT).show();
                    loadProducts(); // 刷新商品列表
                    Integer points = MyApplication.getCurUser().getPoints(); // 刷新积分
                    MyApplication.getCurUser().setPoints(points - product.getPointsRequired());
                    apiService.updateUserInfo(MyApplication.getCurUser()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {

                        }
                    });
                } else {
                    Toast.makeText(PointMallActivity.this, 
                        "兑换失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(PointMallActivity.this, 
                    "网络错误，请重试", Toast.LENGTH_SHORT).show();
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