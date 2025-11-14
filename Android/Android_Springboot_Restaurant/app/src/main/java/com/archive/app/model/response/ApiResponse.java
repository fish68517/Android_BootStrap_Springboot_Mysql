package com.archive.app.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * 通用API响应模型
 * 对应: FavoriteController 和 CommentController 中的 Map<String, Object>
 */
public class ApiResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}