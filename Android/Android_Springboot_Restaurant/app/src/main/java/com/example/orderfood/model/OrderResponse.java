package com.example.orderfood.model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {

    private boolean success;


    private String message;


    private String orderId;


    private String orderNumber;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
} 