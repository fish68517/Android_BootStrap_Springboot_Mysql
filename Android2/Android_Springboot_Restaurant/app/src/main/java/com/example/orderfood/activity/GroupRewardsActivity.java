package com.example.orderfood.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class GroupRewardsActivity extends AppCompatActivity {

    private static final String GROUP_QQ = "123456789";  // QQ群号
    private static final String GROUP_WECHAT = "orderFood888";  // 微信群号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_rewards);

        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("入群有礼");
    }

    private void initViews() {
        // QQ群卡片
        MaterialCardView cardQQ = findViewById(R.id.card_qq);
        TextView tvQQGroup = findViewById(R.id.tv_qq_group);
        MaterialButton btnCopyQQ = findViewById(R.id.btn_copy_qq);
        ImageView ivQQCode = findViewById(R.id.iv_qq_code);

        // 微信群卡片
        MaterialCardView cardWechat = findViewById(R.id.card_wechat);
        TextView tvWechatGroup = findViewById(R.id.tv_wechat_group);
        MaterialButton btnCopyWechat = findViewById(R.id.btn_copy_wechat);
        ImageView ivWechatCode = findViewById(R.id.iv_wechat_code);

        // 设置群号
        tvQQGroup.setText(String.format("QQ群号：%s", GROUP_QQ));
        tvWechatGroup.setText(String.format("微信群号：%s", GROUP_WECHAT));

        // 复制群号
        btnCopyQQ.setOnClickListener(v -> copyToClipboard("QQ群号", GROUP_QQ));
        btnCopyWechat.setOnClickListener(v -> copyToClipboard("微信群号", GROUP_WECHAT));

        // 设置群二维码图片
        ivQQCode.setImageResource(R.drawable.qr_code_qq);
        ivWechatCode.setImageResource(R.drawable.qr_code_qq);
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + "已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 