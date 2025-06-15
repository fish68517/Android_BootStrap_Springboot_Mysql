package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.SpecOption;
import com.google.android.material.chip.Chip;

import java.util.List;

public class SpecOptionAdapter extends RecyclerView.Adapter<SpecOptionAdapter.SpecOptionViewHolder> {
    private List<SpecOption> options;
    private OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(SpecOption option);
    }

    public SpecOptionAdapter(List<SpecOption> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpecOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Chip chip = (Chip) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spec_option, parent, false);
        return new SpecOptionViewHolder(chip);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecOptionViewHolder holder, int position) {
        holder.bind(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class SpecOptionViewHolder extends RecyclerView.ViewHolder {
        private Chip chip;

        SpecOptionViewHolder(Chip chip) {
            super(chip);
            this.chip = chip;
        }

        void bind(SpecOption option) {
            // 设置选项名称和价格
            String text = option.getName();
            if (option.getPriceDelta() > 0) {
                text += String.format(" +¥%.2f", option.getPriceDelta());
            } else if (option.getPriceDelta() < 0) {
                text += String.format(" -¥%.2f", -option.getPriceDelta());
            }
            chip.setText(text);
            
            // 设置选中状态
            chip.setChecked(option.isSelected());
            
            // 设置点击事件
            chip.setOnClickListener(v -> {
                option.setSelected(!option.isSelected());
                chip.setChecked(option.isSelected());
                listener.onOptionClick(option);
            });
        }
    }
} 