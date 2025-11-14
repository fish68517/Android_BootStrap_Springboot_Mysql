package com.gameplatform.model;

/**
 * User entity class representing user information
 * Requirements: 1.2, 2.2, 6.1
 */
public class User {
    private String userId;
    private String phoneNumber;
    private String nickname;
    private String avatar;
    private long registerTime;
    private boolean isLoggedIn;

    public User() {}

    public User(String userId, String phoneNumber, String nickname, String avatar, long registerTime) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.avatar = avatar;
        this.registerTime = registerTime;
        this.isLoggedIn = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", registerTime=" + registerTime +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }
}