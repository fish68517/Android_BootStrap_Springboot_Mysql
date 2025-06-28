package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.Dish;

import com.example.orderfood.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnPayClickListener payClickListener;

    private ApiService apiService = RetrofitClient.getInstance().getApiService();

    public interface OnPayClickListener {
        void onPayClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void setOnPayClickListener(OnPayClickListener listener) {
        this.payClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        
        // 设置订单状态
        holder.statusChip.setText(getStatusText(order.getStatus()));
        holder.statusChip.setChipBackgroundColorResource(getStatusColor(order.getStatus()));
        
        // 设置配送方式
        holder.deliveryChip.setText(order.getOrderType() == 2 ? "预约点单" : "到店消费");
        
        // 设置订单总价
        holder.priceText.setText(String.format("¥%.2f", order.getTotalAmount()));
        
        // 设置订单时间
        holder.timeText.setText("订单号：" + order.getOrderNo());

        // 设置商品列表
        RecyclerView recyclerView = holder.recyclerViewOrderDishes;
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        
        OrderDishAdapter dishAdapter = new OrderDishAdapter(context);
        recyclerView.setAdapter(dishAdapter);

        // 解析商品列表
        String[] cartItems = order.getCartItems().split(";");
        List<Call<Dish>> calls = new ArrayList<>();
        List<OrderDishAdapter.OrderDishItem> dishItems = new ArrayList<>(cartItems.length);
        // 预先分配空间
        for (int i = 0; i < cartItems.length; i++) {
            dishItems.add(null);
        }

        // 创建所有请求
        for (int i = 0; i < cartItems.length; i++) {
            String[] parts = cartItems[i].split(":");
            int dishId = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            String option = parts[2];
            final int index = i;

            System.out.println("dishId: " + dishId + " quantity: " + quantity + " option: " + option);
            apiService.getDishById(dishId).enqueue(new Callback<Dish>() {
                @Override
                public void onResponse(Call<Dish> call, Response<Dish> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Dish dish = response.body();
                        dishItems.set(index, new OrderDishAdapter.OrderDishItem(dish, quantity, option));

                        // 检查是否所有项都已填充
                        boolean allItemsFilled = true;
                        for (OrderDishAdapter.OrderDishItem item : dishItems) {
                            if (item == null) {
                                allItemsFilled = false;
                                break;
                            }
                        }

                        // 所有数据都准备好了才更新适配器
                        if (allItemsFilled) {
                            dishAdapter.setDishes(dishItems);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Dish> call, Throwable t) {
                    // 处理错误，可以设置一个占位项
                    dishItems.set(index, null);
                }
            });
        }

        // 设置支付按钮
        holder.payButton.setVisibility(order.getStatus() == 0 ? View.VISIBLE : View.GONE);
        holder.payButton.setOnClickListener(v -> {
            if (payClickListener != null) {
                payClickListener.onPayClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getStatusText(Integer status) {
        // '0:待支付 1:已支付 2:配送中 3:已完成 4:已取消',
        // 转换为中文状态文本
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "配送中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return status.toString();
        }
    }

    private int getStatusColor(Integer status) {
        // '0:待支付 1:已支付 2:配送中 3:已完成 4:已取消',
        // 转换为颜色资源

        switch (status) {
            case 0: return R.color.status_pending;
            case 1: return R.color.status_paid;
            case 2: return R.color.status_completed;
            case 3: return R.color.status_cancelled;

            default: return R.color.status_pending;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        Chip statusChip;
        Chip deliveryChip;
        TextView timeText;
        ImageView dishImage;
        TextView dishName;
        TextView quantityText;
        TextView priceText;
        MaterialButton payButton;
        RecyclerView recyclerViewOrderDishes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            statusChip = itemView.findViewById(R.id.statusChip);
            deliveryChip = itemView.findViewById(R.id.deliveryChip);
            timeText = itemView.findViewById(R.id.timeText);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishName = itemView.findViewById(R.id.dishName);
            quantityText = itemView.findViewById(R.id.quantityText);
            priceText = itemView.findViewById(R.id.priceText);
            payButton = itemView.findViewById(R.id.payButton);
            recyclerViewOrderDishes = itemView.findViewById(R.id.recyclerViewOrderDishes);
        }
    }
} 