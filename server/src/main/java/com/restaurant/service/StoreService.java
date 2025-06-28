package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.DishCategory;
import com.restaurant.model.Store;

import java.util.List;

public interface StoreService extends IService<Store> {
    List<DishCategory> listCategoriesByStoreId(Integer id);
}