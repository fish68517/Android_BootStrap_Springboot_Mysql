package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("point_exchanges")
public class PointExchange {
    @TableId(type = IdType.AUTO)
    private Integer exchangeId;
    private Integer userId;
    private Integer productId;
    private Integer pointsUsed;
    private Integer status;
    private LocalDateTime createdAt;
} 