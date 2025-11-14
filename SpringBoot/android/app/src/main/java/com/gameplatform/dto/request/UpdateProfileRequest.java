package com.gameplatform.dto.request;

/**
 * Request DTO for updating user profile
 * Requirements: 6.2, 6.3
 */
public class UpdateProfileRequest {
    private String userId;
    private String nickname;
    private String avatar;
    
    public UpdateProfileRequest() {}
    
    public UpdateProfileRequest(String userId, String nickname, String avatar) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}