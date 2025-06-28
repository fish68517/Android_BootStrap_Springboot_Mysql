package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.Review;

import java.util.List;

public interface ReviewService extends IService<Review> {
    Review getByOrderId(Integer orderId);
    List<Review> getByUserId(Integer userId);
    List<Review> getByStoreId(Integer storeId);
} 