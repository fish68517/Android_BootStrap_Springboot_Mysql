package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 评论实体
 * 对应: comments 表
 */
public class Comment {

    @SerializedName("comment_id")
    private int commentId;

    @SerializedName("game_id")
    private int gameId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("content")
    private String content;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("reviewed_by_admin_id")
    private Integer reviewedByAdminId;

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getReviewedByAdminId() {
        return reviewedByAdminId;
    }

    public void setReviewedByAdminId(Integer reviewedByAdminId) {
        this.reviewedByAdminId = reviewedByAdminId;
    }
}