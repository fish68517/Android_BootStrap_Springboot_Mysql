package com.example.orderfood.model;

import java.io.Serializable;
import java.util.List;

public class SpecGroup implements Serializable {
    private int id;
    private String name;
    private int categoryId;
    private int sort;
    private List<SpecOption> options;
    private boolean required;

    // Getters and Setters
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<SpecOption> getOptions() {
        return options;
    }

    public void setOptions(List<SpecOption> options) {
        this.options = options;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
} 