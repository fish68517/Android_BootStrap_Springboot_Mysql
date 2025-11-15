package com.graduation.dto;

// 用于收藏/取消收藏
public class FavoriteRequestDTO {
    private Integer gameId;
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Integer getGameId() {
        return gameId;
    }
    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }


}