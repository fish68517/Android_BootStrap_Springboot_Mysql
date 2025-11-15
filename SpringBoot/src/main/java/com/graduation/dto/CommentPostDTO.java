package com.graduation.dto;

// 用于发表评论
public class CommentPostDTO {
    private Integer gameId;
    private String content;

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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}