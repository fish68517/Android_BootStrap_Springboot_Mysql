package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.ReviewMapper;
import com.restaurant.model.Review;
import com.restaurant.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Review getByOrderId(Integer orderId) {
        return reviewMapper.getByOrderId(orderId);
    }

    @Override
    public List<Review> getByUserId(Integer userId) {
        return reviewMapper.getByUserId(userId);
    }

    @Override
    public List<Review> getByStoreId(Integer storeId) {
        return reviewMapper.getByStoreId(storeId);
    }
} 