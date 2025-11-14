package com.archive.app.model;

/**
 * 个人中心菜单项模型 (用于 ProfileFragment)
 */
public class ProfileMenuItem {
    private String title;
    private int iconResId;

    public ProfileMenuItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}