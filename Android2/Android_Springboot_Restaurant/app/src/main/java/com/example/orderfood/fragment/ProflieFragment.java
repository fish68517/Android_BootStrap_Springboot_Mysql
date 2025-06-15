package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.LoginActivity;
import com.example.orderfood.activity.UpdatePasswordActivity;
import com.example.orderfood.activity.UserInfoActivity;

public class ProflieFragment extends Fragment {


    private TextView username;
    private TextView score;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_priflie, container, false);

        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登录
                getActivity().finish();
                // 退出app
                System.exit(0);
            }
        });

        // 密码修改设置
        username = view.findViewById(R.id.tv_username);
        score = view.findViewById(R.id.tv_id);
        score.setText("积分：" + MyApplication.getCurUser().getPoints() );


        username.setText(MyApplication.getCurUser().getNickname());

        view.findViewById(R.id.ll_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换账号
                handleSwitchAccount();
            }
        });

        view.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 信息完善功能
                // 在initViews方法中添加
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);

            }
        });

        view.findViewById(R.id.ll_orders_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 客服功能
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CustomerServiceFragment fragment = new CustomerServiceFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        view.findViewById(R.id.ll_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登录
                Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        score.setText("积分：" + MyApplication.getCurUser().getPoints() );
    }

    // 处理切换账号点击
    private void handleSwitchAccount() {
        new AlertDialog.Builder(getContext())
                .setTitle("切换账号")
                .setMessage("确定要切换当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 清除当前用户信息
                    MyApplication.saveUser(null);
                    // 跳转到登录页面
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }


}
