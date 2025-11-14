package com.gameplatform.dto.request;

/**
 * Request DTO for adding game to favorites
 * Requirements: 4.1, 4.2
 */
public class FavoriteRequest {
    private String gameId;
    private String userId;
    
    public FavoriteRequest() {}
    
    public FavoriteRequest(String gameId, String userId) {
        this.gameId = gameId;
        this.userId = userId;
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
    
    @Override
    public String toString() {
        return "FavoriteRequest{" +
                "gameId='" + gameId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}