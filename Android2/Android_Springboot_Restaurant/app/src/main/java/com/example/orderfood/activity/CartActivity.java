package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.model.CartItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    private List<CartItem> cartList = new ArrayList<>();
    private TextView textViewTotalPrice;
    private CheckBox checkBoxSelectAll;
    private Button buttonCheckout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart); // Ensure you have this layout file

        setupToolbar();
        initViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getCartItems(MyApplication.getCurUser().getUserId()).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                cartList.clear();
                cartList.addAll(response.body());
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                System.out.println("获取购物车数据失败: " + t.getMessage());
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("购物车");
        // 设置返回键监听
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 初始化控件
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        checkBoxSelectAll = findViewById(R.id.checkBoxSelectAll);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        
        // 设置适配器并传入回调
        cartAdapter = new CartAdapter(cartList, this, new CartAdapter.CartCallback() {
            @Override
            public void onQuantityChanged(int position, int quantity) {
                updateCartItem(position, quantity);
            }

            @Override
            public void onItemSelected(int position, boolean isSelected) {
                updateTotalPrice();
                updateSelectAllStatus();
            }
        });
        recyclerView.setAdapter(cartAdapter);

        // 清空购物车
        findViewById(R.id.textViewClear).setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("清空购物车")
                    .setMessage("确定要清空购物车吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        clearCart();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 全选功能
        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                for (CartItem item : cartList) {
                    item.setSelected(isChecked);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });

        // 结算按钮
        buttonCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "请选择要结算的商品", Toast.LENGTH_SHORT).show();
                return;
            }
            proceedToCheckout(selectedItems);
        });
    }

    private void updateCartItem(int position, int quantity) {
        CartItem item = cartList.get(position);
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        System.out.println("更新购物车数据: " + item.getId() + " " + quantity);
        if (quantity <= 0) {
            // 删除商品
            apiService.removeFromCart( item.getId(),MyApplication.getCurUser().getUserId())
                    .enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            cartList.remove(position);
                            cartAdapter.notifyItemRemoved(position);
                            updateTotalPrice();
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Toast.makeText(CartActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // 更新数量
            double price1 = getTotalPrice(item);
            double totalPrice = price1 * quantity;
            System.out.println("更新购物车数据: " + price1 + "， " + totalPrice );

            apiService.updateCartQuantity(MyApplication.getCurUser().getUserId(), 
                    item.getId(), quantity,totalPrice)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            item.setQuantity(quantity);
                            item.setTotalPrice(totalPrice);
                            cartAdapter.notifyItemChanged(position);
                            updateTotalPrice();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(CartActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private double getTotalPrice(CartItem item) {
        double basePrice = item.getDish().getPrice();
        for (Map.Entry<String,String> entry : item.getSelectedOptions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // ��加该项的总价
            if (key.equals("容量")) {
                if (value.equals("小份")) {
                    basePrice  = basePrice-2;
                }
                else if (value.equals("大份")) {
                    basePrice  = basePrice+2;
                }
            } else if (key.equals("辅料")) {
                if (value.equals("加珍珠")) {
                    basePrice  = basePrice+2;
                }
                else if (value.equals("加椰果")) {
                    basePrice  = basePrice+2;
                }
            }
        }
        return basePrice;
    }

    private void clearCart() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.clearCart(MyApplication.getCurUser().getUserId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        cartList.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "清空失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += item.getTotalPrice();
            }
        }
        textViewTotalPrice.setText(String.format("合计: ¥%.2f", total));
    }

    private void updateSelectAllStatus() {
        boolean allSelected = true;
        for (CartItem item : cartList) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        checkBoxSelectAll.setChecked(allSelected);
    }

    private List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    private void proceedToCheckout(List<CartItem> selectedItems) {
        // TODO: 跳转到结算页面
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("selected_items", (Serializable) selectedItems);
        startActivity(intent);
    }
}