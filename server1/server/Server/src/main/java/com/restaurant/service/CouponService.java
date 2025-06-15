package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.Coupon;

public interface CouponService extends IService<Coupon> {
    /**
     * 根据 couponId 删除优惠券
     * @param couponId 优惠券ID
     * @return 是否删除成功
     */
    boolean removeByCouponId(Integer couponId);
} 