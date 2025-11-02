package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.config.JsonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value = "order_details", autoResultMap = true)
public class OrderDetail {
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Integer detailId;
    
    private Integer orderId;
    private Integer dishId;
    private Integer quantity;
    private BigDecimal price;
    
    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, String> selectedOptions;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    
    @TableField(exist = false)
    private Dish dish; // 关联的菜品信息
} 