package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.DishCategoryMapper;
import com.restaurant.model.DishCategory;
import com.restaurant.service.DishCategoryService;
import org.springframework.stereotype.Service;

@Service
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements DishCategoryService {
} 