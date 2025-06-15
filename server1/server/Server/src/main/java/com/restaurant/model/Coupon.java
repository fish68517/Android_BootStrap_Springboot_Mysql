package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("coupons")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Integer couponId;
    private String couponName;
    private Integer couponType;
    private BigDecimal minAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private Integer validDays;
    private Integer status;
} 