package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 评论点赞实体 (用于表示点赞关系)
 * 对应: comment_likes 表
 */
public class CommentLike {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("comment_id")
    private int commentId;

    @SerializedName("liked_at")
    private Date likedAt;

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public Date getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(Date likedAt) {
        this.likedAt = likedAt;
    }
}