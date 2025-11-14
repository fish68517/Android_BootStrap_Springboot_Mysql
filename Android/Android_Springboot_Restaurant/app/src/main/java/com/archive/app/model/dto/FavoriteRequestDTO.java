package com.archive.app.model.dto;

/**
 * 收藏/取消收藏请求 DTO
 * 对应: FavoriteController.java
 */
public class FavoriteRequestDTO {

    private Integer gameId;

    public FavoriteRequestDTO(Integer gameId) {
        this.gameId = gameId;
    }

    // Getters and Setters
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}