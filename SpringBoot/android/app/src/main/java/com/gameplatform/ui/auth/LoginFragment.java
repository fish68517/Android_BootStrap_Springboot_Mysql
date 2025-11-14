package com.gameplatform.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gameplatform.R;
import com.gameplatform.contract.AuthContract;
import com.gameplatform.presenter.AuthPresenter;

/**
 * Login Fragment
 * Requirements: 1.1, 1.4, 2.1, 2.4
 */
public class LoginFragment extends Fragment {
    
    private TextInputLayout tilPhone;
    private TextInputLayout tilVerificationCode;
    private TextInputEditText etPhone;
    private TextInputEditText etVerificationCode;
    private MaterialButton btnSendCode;
    private MaterialButton btnLogin;
    
    private AuthContract.Presenter presenter;
    private CountDownTimer countDownTimer;
    private boolean isCountingDown = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initPresenter();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tilPhone = view.findViewById(R.id.til_phone);
        tilVerificationCode = view.findViewById(R.id.til_verification_code);
        etPhone = view.findViewById(R.id.et_phone);
        etVerificationCode = view.findViewById(R.id.et_verification_code);
        btnSendCode = view.findViewById(R.id.btn_send_code);
        btnLogin = view.findViewById(R.id.btn_login);
    }
    
    private void initPresenter() {
        if (getActivity() instanceof AuthContract.View) {
            presenter = new AuthPresenter((AuthContract.View) getActivity(), getContext());
        }
    }
    
    private void setupClickListeners() {
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnLogin.setOnClickListener(v -> login());
    }
    
    private void sendVerificationCode() {
        String phoneNumber = etPhone.getText().toString().trim();
        
        // Clear previous errors
        tilPhone.setError(null);
        
        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhone.setError(getString(R.string.error_phone_empty));
            return;
        }
        
        if (presenter != null && !presenter.validatePhoneNumber(phoneNumber)) {
            tilPhone.setError(getString(R.string.error_phone_invalid));
            return;
        }
        
        // Send verification code
        if (presenter != null && !isCountingDown) {
            presenter.sendVerificationCode(phoneNumber, "login");
            startCountdown();
        }
    }
    
    private void login() {
        String phoneNumber = etPhone.getText().toString().trim();
        String verificationCode = etVerificationCode.getText().toString().trim();
        
        // Clear previous errors
        tilPhone.setError(null);
        tilVerificationCode.setError(null);
        
        // Validate inputs
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhone.setError(getString(R.string.error_phone_empty));
            return;
        }
        
        if (presenter != null && !presenter.validatePhoneNumber(phoneNumber)) {
            tilPhone.setError(getString(R.string.error_phone_invalid));
            return;
        }
        
        if (TextUtils.isEmpty(verificationCode)) {
            tilVerificationCode.setError(getString(R.string.error_code_empty));
            return;
        }
        
        if (presenter != null && !presenter.validateVerificationCode(verificationCode)) {
            tilVerificationCode.setError(getString(R.string.error_code_invalid));
            return;
        }
        
        // Perform login
        if (presenter != null) {
            presenter.login(phoneNumber, verificationCode);
        }
    }
    
    private void startCountdown() {
        isCountingDown = true;
        btnSendCode.setEnabled(false);
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                btnSendCode.setText(getString(R.string.countdown_format, seconds));
            }
            
            @Override
            public void onFinish() {
                isCountingDown = false;
                btnSendCode.setEnabled(true);
                btnSendCode.setText(getString(R.string.send_code));
            }
        };
        
        countDownTimer.start();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}