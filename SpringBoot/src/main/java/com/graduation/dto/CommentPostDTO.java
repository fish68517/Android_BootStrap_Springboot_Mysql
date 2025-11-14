package com.graduation.dto;

// 用于发表评论
public class CommentPostDTO {
    private Integer gameId;
    private String content;

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