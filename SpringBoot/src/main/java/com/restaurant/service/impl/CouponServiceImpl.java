package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.CouponMapper;
import com.restaurant.model.Coupon;
import com.restaurant.service.CouponService;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    @Override
    public boolean removeByCouponId(Integer couponId) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coupon_id", couponId); // 假设数据库字段名为 coupon_id
        return remove(queryWrapper);
    }
}