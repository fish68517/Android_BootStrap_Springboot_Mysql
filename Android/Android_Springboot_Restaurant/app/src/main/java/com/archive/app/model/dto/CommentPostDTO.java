package com.archive.app.model.dto;

/**
 * 发表评论请求 DTO
 * 对应: CommentController.java
 */
public class CommentPostDTO {

    private Integer gameId;
    private String content;

    public CommentPostDTO(Integer gameId, String content) {
        this.gameId = gameId;
        this.content = content;
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