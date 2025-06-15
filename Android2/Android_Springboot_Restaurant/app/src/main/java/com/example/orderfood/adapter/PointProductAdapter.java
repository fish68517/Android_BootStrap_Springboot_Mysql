package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.model.PointProduct;

import java.util.ArrayList;
import java.util.List;

public class PointProductAdapter extends RecyclerView.Adapter<PointProductAdapter.ViewHolder> {

    private List<PointProduct> products = new ArrayList<>();
    private OnProductExchangeListener listener;

    public PointProductAdapter(OnProductExchangeListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<PointProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvName;
        private TextView tvDesc;
        private TextView tvPoints;
        private Button btnExchange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPoints = itemView.findViewById(R.id.tv_points);
            btnExchange = itemView.findViewById(R.id.btn_exchange);
        }

        public void bind(PointProduct product) {
            tvName.setText(product.getProductName());
            tvDesc.setText(product.getProductDesc());
            tvPoints.setText(String.format("%d积分", product.getPointsRequired()));
            
            Glide.with(itemView.getContext())
                .load(product.getProductImage())
                .placeholder(R.drawable.default_daijinquan)
                    .error(R.drawable.default_daijinquan)
                .into(ivProduct);

            btnExchange.setEnabled(product.getStock() > 0);
            btnExchange.setText(product.getStock() > 0 ? "立即兑换" : "已售罄");
            
            btnExchange.setOnClickListener(v -> {
                if (product.getStock() > 0 && listener != null) {
                    listener.onExchange(product);
                }
            });
        }
    }

    public interface OnProductExchangeListener {
        void onExchange(PointProduct product);
    }
} 