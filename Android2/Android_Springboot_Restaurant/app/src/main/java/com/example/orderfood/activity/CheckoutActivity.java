package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.CheckoutItemAdapter;
import com.example.orderfood.R;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.OrderDetail;
import com.example.orderfood.model.UserAddress;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private TextView addressText;
    private TextView contactText;
    private TextView merchantName;
    private RecyclerView itemsRecyclerView;
    private RadioGroup deliveryGroup;
    private EditText remarkEdit;
    private TextView totalPriceText;
    private MaterialButton submitButton;
    
    private CheckoutItemAdapter itemsAdapter;
    private List<CartItem> cartItems;
    private double totalPrice;

    private ApiService apiService = RetrofitClient.getInstance().getApiService();
    private RadioButton pickupRadio;
    private RadioButton deliveryRadio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressText = findViewById(R.id.addressText);
        contactText = findViewById(R.id.contactText);
        merchantName = findViewById(R.id.merchantName);
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        deliveryGroup = findViewById(R.id.deliveryGroup);
        pickupRadio = findViewById(R.id.pickupRadio);
        deliveryRadio = findViewById(R.id.deliveryRadio);
        deliveryGroup = findViewById(R.id.deliveryGroup);
        remarkEdit = findViewById(R.id.remarkEdit);
        totalPriceText = findViewById(R.id.totalPriceText);
        submitButton = findViewById(R.id.submitButton);

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new CheckoutItemAdapter(this);
        itemsRecyclerView.setAdapter(itemsAdapter);
    }

    private void loadData() {
        // 获取传递过来的购物车商品
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("selected_items");
        itemsAdapter.setItems(cartItems);


        // 计算总价
        totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
            String cartType = item.getCartType();
            if (cartType.equals("预约点单")) {
                deliveryGroup.check(R.id.deliveryRadio);
                pickupRadio.setVisibility(View.GONE);
            } else if (cartType.equals("到店消费")) {
                // 收藏商品
                // 显示商家名称
                deliveryGroup.check(R.id.pickupRadio);
                deliveryRadio.setVisibility(View.GONE);
            } else {
                deliveryGroup.check(R.id.pickupRadio);
                deliveryRadio.setVisibility(View.GONE);
            }
        }
        totalPriceText.setText(String.format("¥%.2f", totalPrice));

        // 加载用户地址信息
        loadUserAddress();
    }

    private void loadUserAddress() {
        int userId = MyApplication.getCurUser().getUserId();
    }

    private void setupListeners() {
        // 地址卡片点击事件
        findViewById(R.id.addressCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_ADDRESS);
        });

        // 提交订单
        submitButton.setOnClickListener(v -> {
            submitOrder();
        });
    }

    private void submitOrder() {
        Order order = new Order();
        order.setUserId(MyApplication.getCurUser().getUserId());
        order.setStoreId(cartItems.get(0).getDish().getStore().getStoreId());
        order.setOrderStatus(0); //'0:待支付 1:已支付 2:配送中 3:已完成 4:已取消',
        order.setOrderType(deliveryGroup.getCheckedRadioButtonId() == R.id.deliveryRadio ? 2 : 1); //  '1:堂食 2:预约点单',
        order.setTotalAmount(totalPrice);

        // 将购物车商品转换为订单商品列表
        StringBuilder cartItemsbuilder = new StringBuilder();
        for (CartItem item : this.cartItems) {
            if (cartItemsbuilder.length() > 0) {
                cartItemsbuilder.append(";");
            }
            cartItemsbuilder.append(item.getDishId()).append(":").append(item.getQuantity())
                    .append(":").append(item.getSelectedOptions());
        }
        order.setCartItemIds(cartItemsbuilder.toString());

        // 提交订单
        apiService.submitOrder(order).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body()!= null) {
                    // 提交订单详情
                    clearCartItems();

                } else {
                    Toast.makeText(CheckoutActivity.this, "提交订单失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                System.out.println("提交订单失败" + t.getMessage())  ;
                finish();
            }
        });
    }


    private void clearCartItems() {
        final int[] count = {0};
        // 调用 API ��空购物车
        List<Integer> ids = new ArrayList<>();
        for (CartItem item : cartItems) {
            ids.add(item.getId());
            apiService.removeFromCart(item.getId(),MyApplication.getCurUser().getUserId()).
                    enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    // 清空购物车
                    count[0]++;
                    if (count[0] == cartItems.size()) {
                       finish();
                    }

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    System.out.println("删除购物车商品失败" + t.getMessage())  ;

                }
            });
        }
        // 循环调用 API ��删除购物车商品
        // 示例:

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            loadUserAddress();
        }
    }

    private static final int REQUEST_ADDRESS = 1;
} 