package com.example.orderfood.model;


public class User {

    private Integer userId;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private Integer points;
    private String gener;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getPoints() {
        return points == null ? 0 : points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }


    public String getGener() {

        return gener;
    }

    public void setGener(String gener) {
        this.gener = gener;
    }
}