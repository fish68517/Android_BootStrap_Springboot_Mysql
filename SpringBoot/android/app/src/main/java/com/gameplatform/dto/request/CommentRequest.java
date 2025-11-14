package com.gameplatform.dto.request;

/**
 * Request DTO for adding comment
 * Requirements: 5.1, 5.2
 */
public class CommentRequest {
    private String gameId;
    private String userId;
    private String content;
    
    public CommentRequest() {}
    
    public CommentRequest(String gameId, String userId, String content) {
        this.gameId = gameId;
        this.userId = userId;
        this.content = content;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "CommentRequest{" +
                "gameId='" + gameId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}