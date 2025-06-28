package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.Dish;

import java.util.List;
import java.util.Locale;

public class RecommendedDishesAdapter extends RecyclerView.Adapter<RecommendedDishesAdapter.ViewHolder> {

    private final Context context;
    private final List<Dish> dishList;

    public RecommendedDishesAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dish_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvDishName.setText(dish.getDishName());
        holder.tvDishDescription.setText(dish.getDescription());
        holder.tvDishPrice.setText(String.format(Locale.getDefault(), "¥%.2f", dish.getPrice()));

        // Simple image mapping by name
        int imageResId = context.getResources().getIdentifier(dish.getImage(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.ivDishImage.setImageResource(imageResId);
        } else {
            holder.ivDishImage.setImageResource(R.drawable.ic_location); // Fallback image
        }
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDishImage;
        TextView tvDishName, tvDishDescription, tvDishPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDishImage = itemView.findViewById(R.id.iv_dish_image);
            tvDishName = itemView.findViewById(R.id.tv_dish_name);
            tvDishDescription = itemView.findViewById(R.id.tv_dish_description);
            tvDishPrice = itemView.findViewById(R.id.tv_dish_price);
        }
    }
} 