package com.example.orderfood.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.RecommendedDishesAdapter;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.OrderDetail;
import com.example.orderfood.model.Review;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedDishesActivity extends AppCompatActivity {

    private static final String TAG = "菜品推荐";

    private RecyclerView recyclerView;
    private RecommendedDishesAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoRecommendations;

    private ApiService apiService;
    private User currentUser;

    private final List<Dish> recommendedDishes = new ArrayList<>();
    private final Set<Integer> processedDishIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_dishes);

        // 获取 Toolbar 实例
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取返回按钮实例
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理返回按钮点击事件
                Toast.makeText(RecommendedDishesActivity.this, "返回", Toast.LENGTH_SHORT).show();
                finish(); // 关闭当前 Activity
            }
        });

        recyclerView = findViewById(R.id.rv_recommended_dishes);
        progressBar = findViewById(R.id.progress_bar);
        tvNoRecommendations = findViewById(R.id.tv_no_recommendations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendedDishesAdapter(this, recommendedDishes);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getInstance().getApiService();
        currentUser = MyApplication.getCurUser();

        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTopRatedReviews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchTopRatedReviews() {
        Log.d(TAG, "步骤 1: 开始获取用户 " + currentUser.getUserId() + " 的评价列表");
        apiService.getUserReviews(currentUser.getUserId()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d(TAG, "步骤 1: 成功获取到 " + response.body().size() + " 条评价");
                    List<Review> reviews = response.body();
                    // Sort by rating descending
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        reviews.sort((r1, r2) -> Float.compare(r2.getRating(), r1.getRating()));
                    } else {
                        Collections.sort(reviews, (r1, r2) -> Float.compare(r2.getRating(), r1.getRating()));
                    }

                    // Get top 5
                    List<Review> topReviews = reviews.subList(0, Math.min(5, reviews.size()));
                    Log.d(TAG, "步骤 1: 筛选出 " + topReviews.size() + " 条最高评价");
                    fetchOrdersForReviews(topReviews);
                } else {
                    Log.d(TAG, "步骤 1: 获取评价失败或列表为空");
                    showNoRecommendations();
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e(TAG, "步骤 1: 网络请求失败", t);
                showError("获取评价列表失败");
            }
        });
    }

    private void fetchOrdersForReviews(List<Review> reviews) {
        Log.d(TAG, "步骤 2: 开始根据评价获取订单详情");
        final Set<Integer> dishIds = new HashSet<>();
        final AtomicInteger reviewCounter = new AtomicInteger(reviews.size());

        for (Review review : reviews) {
            Log.d(TAG, "步骤 2: 正在获取订单ID: " + review.getOrderId());
            apiService.getOrderById(review.getOrderId()).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Order order = response.body();
                        Log.d(TAG, "步骤 3: 成功获取订单 " + order.getOrderId() + "，开始解析菜品ID");
                        if (order.getCartItems() != null ) {
                            //  "cartItems": "1:1:{容量=中份, 甜度=标准糖};6:1:{饮用=冷饮}",
                            String[] dishIdsStr = order.getCartItems().split(";");
                            for (String dishIdStr : dishIdsStr) {
                                String[] parts = dishIdStr.split(":");
                                if (parts.length > 0) {
                                    try {
                                        int dishId = Integer.parseInt(parts[0]);
                                        Log.d(TAG, "步骤 3: 从订单中发现菜品ID: " + dishId);
                                        dishIds.add(dishId);
                                    } catch (NumberFormatException e) {
                                        Log.w(TAG, "步骤 3: 无效的菜品ID格式: " + parts[0]);
                                    }
                                }
                            }
                        }
                    } else {
                         Log.w(TAG, "步骤 3: 获取订单 " + review.getOrderId() + " 的详情失败");
                    }
                    if (reviewCounter.decrementAndGet() == 0) {
                        Log.d(TAG, "步骤 3: 所有订单处理完毕，共收集到 " + dishIds.size() + " 个不重复的菜品ID");
                        fetchDishesDetails(dishIds);
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Log.e(TAG, "步骤 3: 获取订单 " + review.getOrderId() + " 详情时网络请求失败", t);
                    if (reviewCounter.decrementAndGet() == 0) {
                         Log.d(TAG, "步骤 3: 所有订单处理完毕（包含失败的请求）");
                        fetchDishesDetails(dishIds);
                    }
                }
            });
        }
    }

    private void fetchDishesDetails(Set<Integer> dishIds) {
        if (dishIds.isEmpty()) {
            Log.d(TAG, "步骤 4: 没有需要获取详情的菜品ID，流程结束");
            showNoRecommendations();
            return;
        }
        Log.d(TAG, "步骤 4: 开始获取 " + dishIds.size() + " 个菜品的详情");
        final AtomicInteger dishCounter = new AtomicInteger(dishIds.size());

        for (Integer dishId : dishIds) {
             if (!processedDishIds.add(dishId)) continue; // Avoid reprocessing

            Log.d(TAG, "步骤 4: 正在获取菜品ID: " + dishId + " 的详情");
            apiService.getDishById(dishId).enqueue(new Callback<Dish>() {
                @Override
                public void onResponse(Call<Dish> call, Response<Dish> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "步骤 4: 成功获取菜品 " + response.body().getDishName() + " 的详情");
                        recommendedDishes.add(response.body());
                    } else {
                        Log.w(TAG, "步骤 4: 获取菜品ID " + dishId + " 的详情失败");
                    }
                    if (dishCounter.decrementAndGet() == 0) {
                        Log.d(TAG, "步骤 4: 所有菜品详情获取完毕，共获得 " + recommendedDishes.size() + " 条推荐数据");
                        updateUI();
                    }
                }

                @Override
                public void onFailure(Call<Dish> call, Throwable t) {
                     Log.e(TAG, "步骤 4: 获取菜品 " + dishId + " 详情时网络请求失败", t);
                    if (dishCounter.decrementAndGet() == 0) {
                        Log.d(TAG, "步骤 4: 所有菜品详情获取完毕（包含失败的请求）");
                        updateUI();
                    }
                }
            });
        }
    }

    private void updateUI() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (recommendedDishes.isEmpty()) {
                Log.d(TAG, "最终结果: 推荐列表为空，显示提示信息");
                tvNoRecommendations.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                 Log.d(TAG, "最终结果: 推荐列表有 " + recommendedDishes.size() + " 个菜品，刷新列表");
                tvNoRecommendations.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showNoRecommendations() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            tvNoRecommendations.setVisibility(View.VISIBLE);
        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            tvNoRecommendations.setText("加载推荐失败");
            tvNoRecommendations.setVisibility(View.VISIBLE);
        });
    }
} 