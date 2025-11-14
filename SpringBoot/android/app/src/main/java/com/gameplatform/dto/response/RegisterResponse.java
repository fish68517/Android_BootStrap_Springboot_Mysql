package com.gameplatform.dto.response;

import com.gameplatform.model.User;

/**
 * Response DTO for user registration
 * Requirements: 1.2, 1.4, 2.2, 2.3, 2.5
 */
public class RegisterResponse {
    private User user;
    private String token;
    private long expiresIn;
    
    public RegisterResponse() {}
    
    public RegisterResponse(User user, String token, long expiresIn) {
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
        return "RegisterResponse{" +
                "user=" + user +
                ", token='" + token + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}