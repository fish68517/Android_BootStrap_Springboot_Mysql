package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 推荐实体
 * 对应: recommendations 表
 */
public class Recommendation {

    @SerializedName("recommendation_id")
    private int recommendationId;

    @SerializedName("admin_id")
    private int adminId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("game_id")
    private int gameId;

    @SerializedName("reason")
    private String reason;

    @SerializedName("created_at")
    private Date createdAt;

    // Getters and Setters
    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}