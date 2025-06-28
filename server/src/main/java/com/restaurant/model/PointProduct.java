package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("point_products")
public class PointProduct {
    @TableId(type = IdType.AUTO)
    private Integer productId;
    private String productName;
    private Integer pointsRequired;
    private Integer stock;
    private Integer status;
} 