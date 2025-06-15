package com.example.orderfood.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


public class Order implements Serializable {

    private Integer orderId;
    private Integer userId;
    private Integer storeId;
    private String orderNo;
    private double totalAmount;
    private double deliveryFee;
    private Integer status;  // 0:待支付 1:已支付 2:配送中 3:已完成 4:已取消
    private Integer orderType;
    private String deliveryAddress;
    private Integer paymentMethod;
    private Date createdAt;
    private String remark;

    private Store store;
    private String cartItems;

    private List<OrderDetail> orderDetailsItems;
    private Coupon coupon;
    private Review review;

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public List<OrderDetail> getOrderDetailsItems() {
        return orderDetailsItems;
    }

    public void setOrderDetailsItems(List<OrderDetail> orderDetailsItems) {
        this.orderDetailsItems = orderDetailsItems;
    }

    public String getCartItems() {
        return cartItems;
    }

    public void setCartItems(String cartItems) {
        this.cartItems = cartItems;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Integer getOrderId() {
        return orderId;
    }


    public String getRemark() {
        return remark;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    // '0:待支付 1:已支付 2:配送中 3:已完成 4:已取消',
    public void setOrderStatus(int pending) {
        this.status = pending;
    }

/*    public void setOrderTime(Date date) {
        // 将 Date 转换为 LocalDateTime
        LocalDateTime localDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDateTime = LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
        }
        this.createdAt = localDateTime;
    }*/

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCartItemIds(String toString) {
        cartItems = toString;
    }

    public void setCoupon(Coupon selectedCoupon) {

        this.coupon = selectedCoupon;
    }


    public static class PaymentMethod {
        public static final int WECHAT = 1;
        public static final int ALIPAY = 2;
    }

    // 订单状态枚举
    public static class OrderStatus {
        public static final int PENDING = 0;    // 待支付
        public static final int PAID = 1;       // 已支付
        public static final int PREPARING = 2;   // 制作中
        public static final int COMPLETED = 3;   // 已完成
        public static final int CANCELLED = 4;   // 已取消
    }
}