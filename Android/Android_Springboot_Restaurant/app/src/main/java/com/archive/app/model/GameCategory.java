package com.archive.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 游戏分类模型 (用于 HomeFragment 的横向列表)
 */
public class GameCategory {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public GameCategory(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}