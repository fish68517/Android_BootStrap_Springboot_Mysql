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
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Dish;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<CartItem> orderItems;

    public OrderItemAdapter(List<CartItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_confirm, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewDish;
        private final TextView textViewName;
        private final TextView textViewPrice;
        private final TextView textViewQuantity;
        private final TextView textViewSubtotal;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewSubtotal = itemView.findViewById(R.id.textViewSubtotal);
        }

        void bind(CartItem item) {
            Dish dish = item.getDish();
            textViewName.setText(dish.getDishName());
            textViewPrice.setText(String.format("¥%.2f", dish.getPrice()));
            textViewQuantity.setText(String.format("×%d", item.getQuantity()));
            textViewSubtotal.setText(String.format("¥%.2f", dish.getPrice() * item.getQuantity()));
            
            Glide.with(itemView.getContext())
                    .load(dish.getImage())
                    .into(imageViewDish);
        }
    }
} 