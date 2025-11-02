package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.config.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@TableName("cart_items")
public class CartItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer dishId;
    
    @TableField(exist = false)
    private Dish dish; // 关联的菜品对象
    
    private Integer userId;
    private Integer quantity;

    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, String> selectedOptions;  // 使用 Map 类型
    private BigDecimal totalPrice;
    private String cartType;
    // 修改时间类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public CartItem() {
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

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", dish=" + dish +
                ", userId=" + userId +
                ", quantity=" + quantity +
                ", selectedOptions=" + selectedOptions +
                ", totalPrice=" + totalPrice +
                ", cartType='" + cartType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}