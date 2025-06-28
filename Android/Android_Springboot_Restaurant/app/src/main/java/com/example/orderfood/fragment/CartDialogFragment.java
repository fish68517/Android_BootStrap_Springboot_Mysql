package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CartDialogFragment extends BottomSheetDialogFragment {
    private CartManager cartManager;
    private CartAdapter cartAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartManager = CartManager.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartAdapter();
        recyclerView.setAdapter(cartAdapter);

        view.findViewById(R.id.textViewClear).setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("清空购物车")
                    .setMessage("确定要清空购物车吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        cartManager.clearCart();
                        dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
} 