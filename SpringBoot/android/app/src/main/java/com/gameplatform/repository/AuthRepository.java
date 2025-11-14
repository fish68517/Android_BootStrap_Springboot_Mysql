package com.gameplatform.repository;

import com.gameplatform.base.BaseRepository;
import com.gameplatform.dto.ApiResponse;
import com.gameplatform.dto.request.LoginRequest;
import com.gameplatform.dto.request.RegisterRequest;
import com.gameplatform.dto.request.SendCodeRequest;
import com.gameplatform.dto.response.LoginResponse;
import com.gameplatform.dto.response.RegisterResponse;
import com.gameplatform.network.ApiService;
import com.gameplatform.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for authentication operations
 * Requirements: 1.1, 1.3, 2.1, 2.2
 */
public class AuthRepository extends BaseRepository {
    
    private ApiService apiService;
    
    public AuthRepository() {
        this.apiService = NetworkManager.getInstance().getApiService();
    }
    
    /**
     * Send verification code
     * @param phoneNumber Phone number
     * @param type Type: "login" or "register"
     * @param callback Callback for result
     */
    public void sendVerificationCode(String phoneNumber, String type, RepositoryCallback<Void> callback) {
        SendCodeRequest request = new SendCodeRequest(phoneNumber, type);
        Call<ApiResponse<Void>> call = apiService.sendVerificationCode(request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                handleResponse(response, callback);
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(handleNetworkError(t));
            }
        });
    }
    
    /**
     * Login user
     * @param phoneNumber Phone number
     * @param verificationCode Verification code
     * @param callback Callback for result
     */
    public void login(String phoneNumber, String verificationCode, RepositoryCallback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(phoneNumber, verificationCode);
        Call<ApiResponse<LoginResponse>> call = apiService.login(request);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                handleResponse(response, callback);
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                callback.onError(handleNetworkError(t));
            }
        });
    }
    
    /**
     * Register user
     * @param phoneNumber Phone number
     * @param verificationCode Verification code
     * @param nickname User nickname
     * @param callback Callback for result
     */
    public void register(String phoneNumber, String verificationCode, String nickname, RepositoryCallback<RegisterResponse> callback) {
        RegisterRequest request = new RegisterRequest(phoneNumber, verificationCode, nickname);
        Call<ApiResponse<RegisterResponse>> call = apiService.register(request);
        
        call.enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterResponse>> call, Response<ApiResponse<RegisterResponse>> response) {
                handleResponse(response, callback);
            }
            
            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                callback.onError(handleNetworkError(t));
            }
        });
    }
}