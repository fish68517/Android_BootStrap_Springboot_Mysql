package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stores")
public class Store {
    @TableId(type = IdType.AUTO)
    private Integer storeId;
    private String storeName;
    private String address;
    private String phone;
    private String businessHours;
    private Integer status;
    private LocalDateTime createdAt;
} 