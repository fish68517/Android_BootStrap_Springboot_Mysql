package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

public class UploadAvatarResponse {
    public User user;
    @SerializedName("avatar_url")
    public String avatarUrl;
} 