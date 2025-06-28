package com.restaurant.controller;

import com.restaurant.model.UserCoupon;
import com.restaurant.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-coupons")
public class UserCouponController {
    
    @Autowired
    private UserCouponService userCouponService;

    @GetMapping
    public List<UserCoupon> list() {
        return userCouponService.list();
    }

    @GetMapping("/{id}")
    public UserCoupon getById(@PathVariable Integer id) {
        return userCouponService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody UserCoupon userCoupon) {
        return userCouponService.save(userCoupon);
    }

    @PutMapping
    public boolean update(@RequestBody UserCoupon userCoupon) {
        return userCouponService.updateById(userCoupon);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return userCouponService.removeById(id);
    }
} 