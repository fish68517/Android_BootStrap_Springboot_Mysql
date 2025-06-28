package com.example.orderfood.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.orderfood.R;
import com.example.orderfood.activity.DailyCouponActivity;
import com.example.orderfood.activity.InventoryManagementActivity;
import com.example.orderfood.activity.LuckyWheelActivity;
import com.example.orderfood.activity.PointMallActivity;
import com.example.orderfood.activity.GroupRewardsActivity;
import com.example.orderfood.activity.RecommendedDishesActivity;
import com.example.orderfood.activity.ReviewListActivity;

public class ActivitiesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        // 确柜奖
        view.findViewById(R.id.card_drawer_prize).setOnClickListener(v -> {
            handleDrawerPrize();
        });

        // 领优惠券
        view.findViewById(R.id.card_coupon).setOnClickListener(v -> {
            handleCoupon();
        });

        // 积分商城
        view.findViewById(R.id.card_points_mall).setOnClickListener(v -> {
            handlePointsMall();
        });

        // 入群有礼
        view.findViewById(R.id.card_group_rewards).setOnClickListener(v -> {
            handleGroupRewards();
        });

        // 加盟热线
        view.findViewById(R.id.card_franchise).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InventoryManagementActivity.class);
            startActivity(intent);
        });
    }

    // 处理确柜奖点击
    private void handleDrawerPrize() {
        Intent intent = new Intent(getActivity(), ReviewListActivity.class);
        startActivity(intent);
    }

    // 处理领优惠券点击
    private void handleCoupon() {
        Intent intent = new Intent(getActivity(), DailyCouponActivity.class);
        startActivity(intent);
    }

    // 处理积分商城点击
    private void handlePointsMall() {
        Intent intent = new Intent(getActivity(), PointMallActivity.class);
        startActivity(intent);
    }

    // 处理入群有礼点击
    private void handleGroupRewards() {
        Intent intent = new Intent(getActivity(), RecommendedDishesActivity.class);
        startActivity(intent);
    }

    // 处理加盟热线点击
    private void handleFranchise() {
        new AlertDialog.Builder(getContext())
                .setTitle("加盟热线13523774087")
                .setMessage("确定要拨打加盟热线吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 拨打电话
                    /*Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:10086")); // 替换为实际的加盟热线
                    startActivity(intent);*/
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}