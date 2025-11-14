package com.archive.app.model.dto;

import java.util.List;

/**
 * 保存偏好设置 DTO
 * 对应: UserController.java
 */
public class PreferencesSaveDTO {

    private List<Integer> preferenceIds;

    public PreferencesSaveDTO(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }

    // Getters and Setters
    public List<Integer> getPreferenceIds() {
        return preferenceIds;
    }

    public void setPreferenceIds(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }
}