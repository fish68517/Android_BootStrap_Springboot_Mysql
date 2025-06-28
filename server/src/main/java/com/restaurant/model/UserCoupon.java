package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_coupons")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Integer userCouponId;
    private Integer userId;
    private Integer couponId;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 