package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.model.OrderDetail;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetail> orderItems;

    public OrderDetailAdapter(List<OrderDetail> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewDish;
        private final TextView textViewName;
        private final TextView textViewPrice;
        private final TextView textViewQuantity;
        private final TextView textViewSubtotal;

        OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewSubtotal = itemView.findViewById(R.id.textViewSubtotal);
        }

        void bind(OrderDetail item) {
            // 设置菜品名称
            textViewName.setText(item.getDish().getDishName());
            
            // 设置单价
            textViewPrice.setText(String.format("¥%.2f", item.getPrice()));
            
            // 设置数量
            textViewQuantity.setText(String.format("×%d", item.getQuantity()));
            
            // 设置小计金额
            double subtotal = item.getPrice() * item.getQuantity();
            textViewSubtotal.setText(String.format("¥%.2f", subtotal));
            
            // 加载菜品图片
            if (item.getDish().getImage() != null && !item.getDish().getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getDish().getImage())
                        .placeholder(R.drawable.placeholder_dish)
                        .error(R.drawable.error_dish)
                        .into(imageViewDish);
            } else {
                imageViewDish.setImageResource(R.drawable.placeholder_dish);
            }
        }
    }

    // 更新订单项列表
    public void updateItems(List<OrderDetail> items) {
        this.orderItems = items;
        notifyDataSetChanged();
    }
} 