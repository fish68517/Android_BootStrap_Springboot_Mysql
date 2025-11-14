package com.gameplatform.dto.response;

import com.gameplatform.model.User;

/**
 * Response DTO for user login
 * Requirements: 1.2, 2.2, 2.3, 2.5
 */
public class LoginResponse {
    private User user;
    private String token;
    private long expiresIn;
    
    public LoginResponse() {}
    
    public LoginResponse(User user, String token, long expiresIn) {
        this.user = user;
        this.token = token;
        this.expiresIn = expiresIn;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
                "user=" + user +
                ", token='" + token + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}