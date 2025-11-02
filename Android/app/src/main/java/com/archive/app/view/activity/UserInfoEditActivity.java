package com.archive.app.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.archive.app.MyApplication;
import com.archive.app.RetrofitClient;
import com.archive.app.model.User;
import com.archive.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoEditActivity extends AppCompatActivity {

    private EditText editNickname, editEmail, editPhone;
    private Button btnSave;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private String userId;
    private User currentUser = MyApplication.curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        userId = getIntent().getStringExtra("USER_ID");

        editNickname = findViewById(R.id.edit_nickname);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveUserInfo());

        if (userId != null && !userId.isEmpty()) {
            loadUserInfo(userId);
        } else {
            Toast.makeText(this, "未找到用户ID.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo(String userId) {
        progressBar.setVisibility(View.VISIBLE);

    }

    private void saveUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "无法保存，用户信息未加载.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newNickname = editNickname.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();


        progressBar.setVisibility(View.VISIBLE);

    }
}
