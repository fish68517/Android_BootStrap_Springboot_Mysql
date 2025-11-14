package com.archive.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.GameCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<GameCategory> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // 默认选中第一个 "全部"

    public interface OnCategoryClickListener {
        void onCategoryClick(GameCategory category);
    }

    public CategoryAdapter(Context context, List<GameCategory> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        GameCategory category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // 处理选中状态
        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
            holder.tvCategoryName.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setSelected(false);
            holder.tvCategoryName.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                notifyItemChanged(selectedPosition); // 取消旧的选中
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition); // 高亮新的选中
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}