package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.OrderDetailAdapter;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private String orderId;
    private ApiService apiService;
    private TextView textViewOrderId;
    private TextView textViewOrderStatus;
    private TextView textViewOrderTime;
    private TextView textViewStoreName;
    private TextView textViewTotalAmount;
    private TextView textViewRemark;
    private RecyclerView recyclerViewOrderItems;
    private OrderDetailAdapter adapter;
    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra("orderId");
        apiService = RetrofitClient.getInstance().getApiService();

        setupToolbar();
        initViews();
        loadOrderDetail();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("订单详情");
    }

    private void initViews() {
        textViewOrderId = findViewById(R.id.textViewOrderId);
        textViewOrderStatus = findViewById(R.id.textViewOrderStatus);
        textViewOrderTime = findViewById(R.id.textViewOrderTime);
        textViewStoreName = findViewById(R.id.textViewStoreName);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewRemark = findViewById(R.id.textViewRemark);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadOrderDetail() {
        apiService.getOrderById(Integer.parseInt(orderId)).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    updateUI(response.body());
                } else {
                    Toast.makeText(OrderDetailActivity.this, 
                            "获取订单详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, 
                        "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Order order) {
        textViewOrderId.setText("订单号：" + order.getOrderId());
        textViewOrderStatus.setText(getOrderStatusText(order.getStatus()));
        textViewOrderTime.setText("下单时间：" + order.getCreatedAt());
        textViewStoreName.setText("门店：" + order.getStore().getStoreName());
        textViewTotalAmount.setText(String.format("合计：¥%.2f", order.getTotalAmount()));
        
        if (order.getRemark() != null && !order.getRemark().isEmpty()) {
            textViewRemark.setVisibility(View.VISIBLE);
            textViewRemark.setText("备注：" + order.getRemark());
        } else {
            textViewRemark.setVisibility(View.GONE);
        }

        adapter = new OrderDetailAdapter(order.getOrderDetailsItems());
        recyclerViewOrderItems.setAdapter(adapter);
    }

    private String getOrderStatusText(int status) {
        switch (status) {
            case Order.OrderStatus.PENDING:
                return "待支付";
            case Order.OrderStatus.PAID:
                return "已支付";
            case Order.OrderStatus.PREPARING:
                return "制作中";
            case Order.OrderStatus.COMPLETED:
                return "已完成";
            case Order.OrderStatus.CANCELLED:
                return "已取消";
            default:
                return "未知状态";
        }
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