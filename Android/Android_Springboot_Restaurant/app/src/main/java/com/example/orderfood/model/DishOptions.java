package com.example.orderfood.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DishOptions implements Serializable {
    private Map<String, List<String>> options;

    public Map<String, List<String>> getOptions() {
        return options;
    }

    public void setOptions(Map<String, List<String>> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "DishOptions{" +
                "options=" + options +
                '}';
    }
}