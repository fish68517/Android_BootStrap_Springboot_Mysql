package com.archive.app.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * 点赞/取消点赞的特定响应
 * 对应: CommentController 中返回 "likeCount" 的 Map
 */
public class LikeResponse extends ApiResponse {

    @SerializedName("likeCount")
    private int likeCount;

    // Getters and Setters
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}