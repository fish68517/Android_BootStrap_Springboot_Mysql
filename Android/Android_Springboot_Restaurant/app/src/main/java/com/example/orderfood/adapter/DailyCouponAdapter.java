package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.Coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DailyCouponAdapter extends RecyclerView.Adapter<DailyCouponAdapter.ViewHolder> {

    private List<Coupon> coupons = new ArrayList<>();
    private OnCouponClaimListener listener;

    public DailyCouponAdapter(OnCouponClaimListener listener) {
        this.listener = listener;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.bind(coupon);
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvValidDays;
        private Button btnClaim;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_coupon_name);
            tvDescription = itemView.findViewById(R.id.tv_coupon_desc);
            tvValidDays = itemView.findViewById(R.id.tv_valid_days);
            btnClaim = itemView.findViewById(R.id.btn_claim);
        }

        public void bind(Coupon coupon) {
            tvName.setText(coupon.getCouponName());
            
            String description;
            if (coupon.getCouponType() == 1) {
                description = String.format("满%s减%s", 
                    coupon.getMinAmount(), coupon.getDiscountAmount());
            } else {
                description = String.format("满%s打%s折", 
                    coupon.getMinAmount(), coupon.getDiscountRate().multiply(new BigDecimal("10")));
            }
            tvDescription.setText(description);
            
            tvValidDays.setText(String.format("有效期%d天", coupon.getValidDays()));
            
            btnClaim.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClaim(coupon);
                }
            });
        }
    }

    public interface OnCouponClaimListener {
        void onClaim(Coupon coupon);
    }
} 