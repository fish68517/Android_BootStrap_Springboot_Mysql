package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 偏好实体
 * 对应: preferences 表
 */
public class Preference {

    @SerializedName("preference_id")
    private int preferenceId;

    @SerializedName("preference_name")
    private String preferenceName;

    // Getters and Setters
    public int getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }
}