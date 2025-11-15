package com.archive.app.model.dto;

/**
 * 收藏/取消收藏请求 DTO
 * 对应: FavoriteController.java
 */
public class FavoriteRequestDTO {

    private Integer gameId;
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public FavoriteRequestDTO(Integer gameId, Integer userId) {
        this.gameId = gameId;
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