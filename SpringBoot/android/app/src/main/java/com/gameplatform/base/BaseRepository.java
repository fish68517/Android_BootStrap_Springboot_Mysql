package com.gameplatform.base;

import com.gameplatform.dto.ApiResponse;
import com.gameplatform.network.ApiService;
import com.gameplatform.network.NetworkManager;

import retrofit2.Response;

/**
 * Base repository class for data access
 * Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1
 */
public abstract class BaseRepository {
    
    protected ApiService apiService;
    
    public BaseRepository() {
        this.apiService = NetworkManager.getInstance().getApiService();
    }
    
    /**
     * Repository callback interface
     * @param <T> Data type
     */
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
    
    /**
     * Handle API response
     * @param response Retrofit response
     * @param callback Repository callback
     * @param <T> Data type
     */
    protected <T> void handleResponse(Response<ApiResponse<T>> response, RepositoryCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                callback.onSuccess(apiResponse.getData());
            } else {
                callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "请求失败");
            }
        } else {
            callback.onError("服务器异常，请稍后重试");
        }
    }
    
    /**
     * Handle network error
     * @param throwable Error throwable
     * @return User-friendly error message
     */
    protected String handleNetworkError(Throwable throwable) {
        if (throwable instanceof java.net.UnknownHostException) {
            return "网络连接失败，请检查网络设置";
        } else if (throwable instanceof java.net.SocketTimeoutException) {
            return "网络请求超时，请重试";
        } else if (throwable instanceof java.io.IOException) {
            return "网络异常，请重试";
        } else {
            return throwable.getMessage() != null ? throwable.getMessage() : "未知错误";
        }
    }
    
    /**
     * Handle API error response
     * @param throwable Error throwable
     * @return User-friendly error message
     */
    protected String handleError(Throwable throwable) {
        return handleNetworkError(throwable);
    }
    
    /**
     * Check if response is successful
     * @param code Response code
     * @return true if successful, false otherwise
     */
    protected boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }
}