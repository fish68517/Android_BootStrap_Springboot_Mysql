package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.UserCouponMapper;
import com.restaurant.model.UserCoupon;
import com.restaurant.service.UserCouponService;
import org.springframework.stereotype.Service;

@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {
} 