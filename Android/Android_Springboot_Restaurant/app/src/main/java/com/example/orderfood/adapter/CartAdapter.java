package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.activity.CartActivity;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Dish;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartCallback callback;

    public interface CartCallback {
        void onQuantityChanged(int position, int quantity);
        void onItemSelected(int position, boolean isSelected);
    }

    public CartAdapter() {
    }

    public CartAdapter(List<CartItem> cartItems, Context context, CartCallback callback) {
        this.cartItems = cartItems;
        this.context = context;
        this.callback = callback;
    }

    public CartAdapter(List<CartItem> cartList,  Context context) {
        this.cartItems = cartList;
        this.context = context;
    }

    public void updateItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewDish;
        private final TextView textViewName;
        private final TextView textViewPrice;
        private final TextView textViewQuantity;
        private final CheckBox checkBox;
        private final ImageButton buttonMinus;
        private final ImageButton buttonPlus;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            checkBox = itemView.findViewById(R.id.checkBox);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);

            buttonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int quantity = cartItems.get(position).getQuantity();
                        quantity--;
                        cartItems.get(position).setQuantity(quantity);
                        notifyItemChanged(position);
                        callback.onQuantityChanged(position, quantity);
                    }
                }
            });

            buttonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int quantity = cartItems.get(position).getQuantity();
                        quantity++;
                        cartItems.get(position).setQuantity(quantity);
                        notifyItemChanged(position);
                        callback.onQuantityChanged(position, quantity);
                    }
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        boolean isSelected = cartItems.get(position).isSelected();
                        cartItems.get(position).setSelected(!isSelected);
                        notifyItemChanged(position);
                        callback.onItemSelected(position,!isSelected);
                    }
                }
            });
        }

        void bind(CartItem item) {
            Dish dish = item.getDish();
            textViewName.setText(dish.getDishName());
            textViewPrice.setText(String.format("¥%.2f", item.getTotalPrice()));
            textViewQuantity.setText(String.valueOf(item.getQuantity()));
            checkBox.setChecked(item.isSelected());
            // 加载 res/drawable/ 图片 请转换：dish.getImage() -> R.drawable.xxx
            // 加载网络图片请使用 Glide.with(itemView.getContext()).load(dish.getImage()).into(imageViewDish);
            // 通过 getResources().getIdentifier() 方法获取图片资源 ID
            int imageResourceId = context.getResources().getIdentifier(dish.getImage(),
                    "drawable", context.getPackageName());
            Glide.with(itemView.getContext())
                    .load(imageResourceId)
                    .into(imageViewDish);
        }
    }
} 