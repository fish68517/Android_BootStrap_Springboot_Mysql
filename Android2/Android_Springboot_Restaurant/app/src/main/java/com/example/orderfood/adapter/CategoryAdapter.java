package com.example.orderfood.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.DishCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<DishCategory> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(List<DishCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DishCategory category = categories.get(position);
        holder.bind(category, position == selectedPosition);
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public void setCategories(List<DishCategory> body) {
        categories.clear();
        categories.addAll(body);
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategory;
        View indicator;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            indicator = itemView.findViewById(R.id.indicator);
        }

        void bind(DishCategory category, boolean isSelected) {
            textViewCategory.setText(category.getCategoryName());
            indicator.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
            itemView.setBackgroundResource(isSelected ? 
                    R.drawable.bg_category_selected : R.drawable.bg_category_normal);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(DishCategory category);
    }
} 