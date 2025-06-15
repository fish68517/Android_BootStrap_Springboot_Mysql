package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.ReviewListAdapter;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.Review;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewListActivity extends AppCompatActivity implements ReviewListAdapter.OnReviewButtonClickListener, ReviewListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ReviewListAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private ApiService apiService;
    private User user = MyApplication.getCurUser();
    private TextView tvNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // 获取 Toolbar 实例
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取返回按钮实例
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理返回按钮点击事件
                Toast.makeText(ReviewListActivity.this, "返回", Toast.LENGTH_SHORT).show();
                finish(); // 关闭当前 Activity
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        tvNoOrders = findViewById(R.id.tv_no_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getInstance().getApiService();

        loadCompletedOrders();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadCompletedOrders() {
        int userId = user.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserOrders(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> completedOrders;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        completedOrders = response.body().stream()
                                .filter(order -> order.getStatus() == Order.OrderStatus.PAID)
                                .collect(Collectors.toList());
                    } else {
                        completedOrders = new ArrayList<>();
                        for (Order order : response.body()) {
                            if (order.getStatus() != null && order.getStatus() == Order.OrderStatus.PAID) {
                                completedOrders.add(order);
                            }
                        }
                    }

                    if (completedOrders.isEmpty()) {
                        tvNoOrders.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoOrders.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        orderList.clear();
                        orderList.addAll(completedOrders);
                        setupRecyclerView();
                    }
                } else {
                    Toast.makeText(ReviewListActivity.this, "加载订单失败", Toast.LENGTH_SHORT).show();
                    tvNoOrders.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(ReviewListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvNoOrders.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ReviewListAdapter(this, orderList, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onReviewButtonClick(Order order) {
        showReviewDialog(order);
    }

    @Override
    public void onItemClick(Order order) {
        if (order.getReview() != null) {
            Intent intent = new Intent(this, ReviewDetailActivity.class);
            intent.putExtra(ReviewDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
            intent.putExtra(ReviewDetailActivity.EXTRA_ORDER_NO, order.getOrderNo());
            startActivity(intent);
        } else {
            Toast.makeText(this, "该订单尚未评价，请先评价", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReviewDialog(final Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);

        final RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        final EditText etComment = dialogView.findViewById(R.id.et_comment);

        builder.setPositiveButton("提交", (dialog, which) -> {
            float rating = ratingBar.getRating();
            String comment = etComment.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(this, "评价内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReview(order, rating, comment);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void submitReview(Order order, float rating, String comment) {
        Review review = new Review();
        review.setOrderId(order.getOrderId());
        review.setUserId(user.getUserId());
        review.setStoreId(order.getStoreId());
        review.setRating(rating);
        review.setComment(comment);

        apiService.createReview(review).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ReviewListActivity.this, "评价成功", Toast.LENGTH_SHORT).show();
                    loadCompletedOrders();
                } else if (response.code() == 409) {
                    Toast.makeText(ReviewListActivity.this, "该订单已经评价过了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReviewListActivity.this, "评价失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ReviewListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 