package com.archive.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.ProfileMenuItem;

import java.util.List;

public class ProfileMenuAdapter extends RecyclerView.Adapter<ProfileMenuAdapter.MenuViewHolder> {

    private Context context;
    private List<ProfileMenuItem> menuItems;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(ProfileMenuItem item);
    }

    public ProfileMenuAdapter(Context context, List<ProfileMenuItem> menuItems, OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        ProfileMenuItem item = menuItems.get(position);
        holder.tvMenuTitle.setText(item.getTitle());
        holder.ivMenuIcon.setImageResource(item.getIconResId());

        holder.itemView.setOnClickListener(v -> listener.onMenuItemClick(item));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMenuIcon;
        TextView tvMenuTitle;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            tvMenuTitle = itemView.findViewById(R.id.tv_menu_title);
        }
    }
}