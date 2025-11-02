package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.PointProductMapper;
import com.restaurant.model.PointProduct;
import com.restaurant.service.PointProductService;
import org.springframework.stereotype.Service;

@Service
public class PointProductServiceImpl extends ServiceImpl<PointProductMapper, PointProduct> implements PointProductService {
} 