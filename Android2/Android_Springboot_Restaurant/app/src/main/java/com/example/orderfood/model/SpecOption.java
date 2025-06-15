package com.example.orderfood.model;

import java.io.Serializable;

public class SpecOption implements Serializable {
    private int id;
    private int groupId;
    private String name;
    private double priceDelta;
    private int sort;
    private boolean selected;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceDelta() {
        return priceDelta;
    }

    public void setPriceDelta(double priceDelta) {
        this.priceDelta = priceDelta;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
} 