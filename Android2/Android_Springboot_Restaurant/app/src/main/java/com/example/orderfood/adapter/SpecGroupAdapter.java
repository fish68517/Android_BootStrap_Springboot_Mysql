package com.example.orderfood.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.SpecGroup;
import com.example.orderfood.model.SpecOption;

import java.util.List;

public class SpecGroupAdapter extends RecyclerView.Adapter<SpecGroupAdapter.SpecGroupViewHolder> {
    private List<SpecGroup> specGroups;
    private OnSpecSelectedListener listener;
    private SparseBooleanArray expandStates = new SparseBooleanArray();

    public interface OnSpecSelectedListener {
        void onSpecSelected(SpecGroup group, SpecOption option);
    }

    public SpecGroupAdapter(List<SpecGroup> specGroups, OnSpecSelectedListener listener) {
        this.specGroups = specGroups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpecGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spec_group, parent, false);
        return new SpecGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecGroupViewHolder holder, int position) {
        holder.bind(specGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return specGroups.size();
    }

    class SpecGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewGroupName;
        private RecyclerView recyclerViewOptions;
        private ImageView imageViewArrow;
        private View layoutHeader;
        private SpecOptionAdapter optionAdapter;

        SpecGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
            recyclerViewOptions = itemView.findViewById(R.id.recyclerViewOptions);
            imageViewArrow = itemView.findViewById(R.id.imageViewArrow);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            recyclerViewOptions.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        void bind(SpecGroup group) {
            textViewGroupName.setText(group.getName());
            
            // 设置展开/收起状态
            boolean isExpanded = expandStates.get(getAdapterPosition(), true);
            recyclerViewOptions.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            imageViewArrow.setRotation(isExpanded ? 180 : 0);

            // 设置点击事件
            layoutHeader.setOnClickListener(v -> {
                boolean expanded = expandStates.get(getAdapterPosition(), true);
                expandStates.put(getAdapterPosition(), !expanded);
                
                // 添加动画效果
                if (!expanded) {
                    recyclerViewOptions.setVisibility(View.VISIBLE);
                    imageViewArrow.animate().rotation(180).setDuration(200).start();
                } else {
                    recyclerViewOptions.setVisibility(View.GONE);
                    imageViewArrow.animate().rotation(0).setDuration(200).start();
                }
            });
            
            optionAdapter = new SpecOptionAdapter(group.getOptions(), option -> {
                // 如果是单选，取消其他选项
                if (!group.getName().contains("配料")) {
                    for (SpecOption opt : group.getOptions()) {
                        if (opt != option) {
                            opt.setSelected(false);
                        }
                    }
                    optionAdapter.notifyDataSetChanged();
                }
                listener.onSpecSelected(group, option);
            });
            recyclerViewOptions.setAdapter(optionAdapter);
        }
    }
} 