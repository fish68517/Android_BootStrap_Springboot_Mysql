package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.orderfood.R;
import com.example.orderfood.activity.CartActivity;
import com.example.orderfood.activity.DineInActivity;
import com.example.orderfood.activity.PreOrderActivity;
import com.example.orderfood.adapter.BannerAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutIndicator;
    private Handler autoScrollHandler;
    private final int AUTO_SCROLL_DELAY = 3000; // 3秒自动滚动
    private List<Integer> bannerImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupBanner();
        setupClickListeners(view);
        setupInfiniteScroll();
        return view;
    }

    private void initViews(View view) {
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);
        
        // 初始化广告图片列表
        bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.jiushui_baixiangguo);
        bannerImages.add(R.drawable.jiushui_natie);
        bannerImages.add(R.drawable.jiushui_baitaowulong);
        bannerImages.add(R.drawable.jiushui_mitaosijichun);
        bannerImages.add(R.drawable.jiushui_fengmiyouzi);
    }

    private void setupBanner() {
        BannerAdapter adapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(adapter);

        // 设置无限循环
        viewPagerBanner.setCurrentItem(1);
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                resetAutoScroll();
            }
        });

        // 设置指示器
        new TabLayoutMediator(tabLayoutIndicator, viewPagerBanner,
                (tab, position) -> {
                    // 不需要设置标题
                }).attach();

        // 启动自动滚动
        startAutoScroll();
    }

    private void setupClickListeners(View view) {
        MaterialCardView cardPreOrder = view.findViewById(R.id.cardPreOrder);
        MaterialCardView cardDineIn = view.findViewById(R.id.cardDineIn);
        MaterialCardView cart = view.findViewById(R.id.cart);

        cardPreOrder.setOnClickListener(v -> {
            navigateToPreOrder();
        });

        cardDineIn.setOnClickListener(v -> {

            navigateToDineIn();
        });

        cart.setOnClickListener(v -> {
            navigateToCart();
        });
    }

    private void navigateToPreOrder() {
        // 跳转到预约点单页面
        Intent intent = new Intent(getActivity(), PreOrderActivity.class);
        startActivity(intent);
    }

    private void navigateToDineIn() {
        // 跳转到到店消费页面
        Intent intent = new Intent(getActivity(), PreOrderActivity.class);
        intent.putExtra("isDineIn", true);
        startActivity(intent);
    }

    private void navigateToCart() {
        // 跳转到到店消费页面
        Intent intent = new Intent(getActivity(), CartActivity.class);
        startActivity(intent);
    }

    private void startAutoScroll() {
        autoScrollHandler = new Handler();
        autoScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerBanner.getCurrentItem();
                viewPagerBanner.setCurrentItem(currentItem + 1);
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        }, AUTO_SCROLL_DELAY);
    }

    private void resetAutoScroll() {
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
            startAutoScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }

    // 添加无限循环滚动功能
    private void setupInfiniteScroll() {
        int realCount = bannerImages.size();
        // 在首尾各添加一张图片
        bannerImages.add(0, bannerImages.get(realCount - 1));
        bannerImages.add(bannerImages.get(1));
        
        viewPagerBanner.setCurrentItem(1, false);
        
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                resetAutoScroll();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (viewPagerBanner.getCurrentItem() == 0) {
                        viewPagerBanner.setCurrentItem(bannerImages.size() - 2, false);
                    } else if (viewPagerBanner.getCurrentItem() == bannerImages.size() - 1) {
                        viewPagerBanner.setCurrentItem(1, false);
                    }
                }
            }
        });
    }
} 