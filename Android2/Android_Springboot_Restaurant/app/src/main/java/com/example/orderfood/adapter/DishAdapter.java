package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.model.Dish;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private final Context context;
    private List<Dish> dishes;
    private OnDishClickListener listener;

    public DishAdapter(List<Dish> dishes, OnDishClickListener listener, Context context) {
        this.dishes = dishes;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.bind(dish);
        holder.itemView.setOnClickListener(v -> listener.onDishClick(dish));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public void setOnAddToCartListener(Dish o) {

    }

    public void setDishes(List<Dish> body) {
        dishes.clear();
        dishes.addAll(body);
        notifyDataSetChanged();

    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        ImageView imageViewDish;
        TextView textViewName;
        TextView textViewPrice;
        MaterialButton buttonAdd;

        DishViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonAdd.setVisibility(View.GONE);
            this.context = context;
        }

        void bind(Dish dish) {
            textViewName.setText(dish.getDishName());
            textViewPrice.setText(String.format("¥%.2f", dish.getPrice()));
            int imageResourceId = context.getResources().getIdentifier(dish.getImage(),
                    "drawable", context.getPackageName());
            Glide.with(itemView.getContext())
                    .load(imageResourceId)
                    .into(imageViewDish);
        }
    }

    public interface OnDishClickListener {
        void onDishClick(Dish dish);
    }
} 