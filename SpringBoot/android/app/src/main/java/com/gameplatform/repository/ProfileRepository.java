package com.gameplatform.repository;

import com.gameplatform.base.BaseRepository;
import com.gameplatform.dto.request.UpdateProfileRequest;
import com.gameplatform.dto.response.ApiResponse;
import com.gameplatform.model.User;
import com.gameplatform.network.ApiService;
import com.gameplatform.network.NetworkManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Profile Repository - Handle profile data operations
 * Requirements: 6.1, 6.2, 6.3
 */
public class ProfileRepository extends BaseRepository {

    private static final String TAG = "ProfileRepository";
    
    private ApiService apiService;

    public ProfileRepository() {
        this.apiService = NetworkManager.getInstance().getApiService();
    }

    /**
     * Get user profile from server
     */
    public void getUserProfile(ProfileCallback callback) {
        Call<ApiResponse<User>> call = apiService.getUserProfile();
        
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                handleError(t, callback);
            }
        });
    }

    /**
     * Update user nickname
     */
    public void updateNickname(String nickname, UpdateCallback callback) {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname(nickname);
        
        Call<ApiResponse<User>> call = apiService.updateProfile(request);
        
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                handleUpdateResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                handleError(t, callback);
            }
        });
    }

    /**
     * Update user avatar
     */
    public void updateAvatar(String avatarPath, AvatarCallback callback) {
        File file = new File(avatarPath);
        if (!file.exists()) {
            callback.onError("头像文件不存在");
            return;
        }
        
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part avatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        
        Call<ApiResponse<String>> call = apiService.uploadAvatar(avatar);
        
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("头像上传失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                handleError(t, callback);
            }
        });
    }

    private void handleResponse(Response<ApiResponse<User>> response, ProfileCallback callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<User> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                callback.onSuccess(apiResponse.getData());
            } else {
                String errorMessage = apiResponse.getMessage();
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "获取用户信息失败";
                }
                callback.onError(errorMessage);
            }
        } else {
            String errorMessage = "获取用户信息失败";
            if (response.code() == 401) {
                errorMessage = "登录已过期，请重新登录";
            } else if (response.code() == 403) {
                errorMessage = "没有权限访问";
            } else if (response.code() >= 500) {
                errorMessage = "服务器异常，请稍后重试";
            }
            callback.onError(errorMessage);
        }
    }

    private void handleUpdateResponse(Response<ApiResponse<User>> response, UpdateCallback callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<User> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                callback.onSuccess(apiResponse.getData());
            } else {
                String errorMessage = apiResponse.getMessage();
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "更新用户信息失败";
                }
                callback.onError(errorMessage);
            }
        } else {
            String errorMessage = "更新用户信息失败";
            if (response.code() == 401) {
                errorMessage = "登录已过期，请重新登录";
            } else if (response.code() == 403) {
                errorMessage = "没有权限修改";
            } else if (response.code() == 409) {
                errorMessage = "昵称已被使用，请选择其他昵称";
            } else if (response.code() >= 500) {
                errorMessage = "服务器异常，请稍后重试";
            }
            callback.onError(errorMessage);
        }
    }

    private void handleError(Throwable t, Object callback) {
        String errorMessage = getErrorMessage(t);
        if (callback instanceof ProfileCallback) {
            ((ProfileCallback) callback).onError(errorMessage);
        } else if (callback instanceof UpdateCallback) {
            ((UpdateCallback) callback).onError(errorMessage);
        } else if (callback instanceof AvatarCallback) {
            ((AvatarCallback) callback).onError(errorMessage);
        }
    }

    // Callback interfaces
    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface AvatarCallback {
        void onSuccess(String avatarUrl);
        void onError(String error);
    }
}