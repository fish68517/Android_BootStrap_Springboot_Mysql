package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dish_categories")
public class DishCategory {
    @TableId(type = IdType.AUTO)
    private Integer categoryId;
    private String categoryName;
    private Integer sortOrder;
    private Integer status;
} 