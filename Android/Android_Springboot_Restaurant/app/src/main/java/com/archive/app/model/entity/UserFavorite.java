package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 用户收藏实体 (用于表示收藏关系)
 * 对应: user_favorites 表
 */
public class UserFavorite {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("game_id")
    private int gameId;

    @SerializedName("favorited_at")
    private Date favoritedAt;

    // Getters and Setters
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

    public Date getFavoritedAt() {
        return favoritedAt;
    }

    public void setFavoritedAt(Date favoritedAt) {
        this.favoritedAt = favoritedAt;
    }
}