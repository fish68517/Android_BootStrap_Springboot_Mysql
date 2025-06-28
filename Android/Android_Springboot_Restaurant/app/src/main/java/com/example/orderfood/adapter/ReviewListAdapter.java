package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final OnReviewButtonClickListener reviewButtonClickListener;
    private final OnItemClickListener itemClickListener;

    public interface OnReviewButtonClickListener {
        void onReviewButtonClick(Order order);
    }

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    public ReviewListAdapter(Context context, List<Order> orderList, OnReviewButtonClickListener reviewButtonClickListener, OnItemClickListener itemClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.reviewButtonClickListener = reviewButtonClickListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("订单号: " + order.getOrderNo());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if(order.getCreatedAt() != null) {
            holder.tvOrderDate.setText("下单时间: " + sdf.format(order.getCreatedAt()));
        }
        holder.tvOrderTotal.setText(String.format(Locale.getDefault(), "总金额: ¥%.2f", order.getTotalAmount()));

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(order);
            }
        });

        if (order.getReview() != null) {
            holder.btnReview.setText("查看评价");
            holder.btnReview.setEnabled(true);
            holder.btnReview.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(order);
                }
            });
        } else {
            holder.btnReview.setText("评价");
            holder.btnReview.setEnabled(true);
            holder.btnReview.setOnClickListener(v -> {
                if (reviewButtonClickListener != null) {
                    reviewButtonClickListener.onReviewButtonClick(order);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal;
        Button btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnReview = itemView.findViewById(R.id.btn_review);
        }
    }
} 