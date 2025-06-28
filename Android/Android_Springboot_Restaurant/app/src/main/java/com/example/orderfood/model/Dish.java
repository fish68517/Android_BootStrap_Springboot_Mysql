package com.example.orderfood.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Dish implements Serializable {

    private Integer dishId;
    private Integer categoryId;
    private String dishName;
    private double price;
    private String description;
    private String image;
    private Integer status;
    private Date createdAt;
    private String dishOptions;

    private Store store;

    public Store getStore() {
        return store == null? new Store() : store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Integer getDishId() {
        return dishId;
    }

    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        if (TextUtils.isEmpty(image)) {
            return "jiushui_mitaosijichun";
        }
        // 去掉 .jpg .png 等后缀
        if (image.contains(".")) {
            return image.substring(0, image.lastIndexOf("."));
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDishOptions() {
        return dishOptions;
    }

    public void setDishOptions(String dishOptions) {
        this.dishOptions = dishOptions;
    }



    @Override
    public String toString() {
        return "Dish{" +
                "dishId=" + dishId +
                ", categoryId=" + categoryId +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +

                ", dishOptions=" + dishOptions +
                '}';
    }
}