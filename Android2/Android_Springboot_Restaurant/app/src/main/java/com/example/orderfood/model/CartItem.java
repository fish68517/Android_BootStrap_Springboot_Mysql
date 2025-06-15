package com.example.orderfood.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


public class CartItem implements Serializable {

    private Integer id;
    
    private Integer dishId;
    

    private Dish dish; // 关联的菜品对象
    
    private Integer userId;
    private Integer quantity;
    private Map<String, String>  selectedOptions; // JSON字符串
    private BigDecimal totalPrice;
    private String cartType;
    private Date createdAt;
    private Date updatedAt;
    private boolean selected;

    public CartItem() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCartType() {
        return cartType;
    }

    public void setCartType(String cartType) {
        this.cartType = cartType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public CartItem(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Map<String, String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<String, String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public double getTotalPrice() {
        return totalPrice.doubleValue();
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = BigDecimal.valueOf(totalPrice);
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 获取规格文本描述
    public String getOptionsText() {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : selectedOptions.entrySet()) {
            if (sb.length() > 0) {
                sb.append("，");
            }
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}