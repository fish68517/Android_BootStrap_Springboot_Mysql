package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.model.Dish;

import java.util.ArrayList;
import java.util.List;

public class OrderDishAdapter extends RecyclerView.Adapter<OrderDishAdapter.ViewHolder> {
    private List<OrderDishItem> dishes;
    private Context context;

    public static class OrderDishItem {
        public Dish dish;
        public int quantity;
        public String option;

        public OrderDishItem(Dish dish, int quantity, String option) {
            this.dish = dish;
            this.quantity = quantity;
            this.option = option;
        }
    }

    public OrderDishAdapter(Context context) {
        this.context = context;
        this.dishes = new ArrayList<>();
    }

    public void setDishes(List<OrderDishItem> dishes) {
        this.dishes = dishes;
        System.out.println("setDishes: " + dishes.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDishItem item = dishes.get(position);
        holder.dishName.setText(item.dish.getDishName());
        holder.optionText.setText(item.option);
        holder.quantityText.setText(String.format("x%d", item.quantity));

        int imageResourceId = context.getResources().getIdentifier(
                item.dish.getImage(), "drawable", context.getPackageName());
        Glide.with(context)
                .load(imageResourceId)
                .into(holder.dishImage);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName;
        TextView optionText;
        TextView quantityText;

        ViewHolder(View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishName = itemView.findViewById(R.id.dishName);
            optionText = itemView.findViewById(R.id.optionText);
            quantityText = itemView.findViewById(R.id.quantityText);
        }
    }
}