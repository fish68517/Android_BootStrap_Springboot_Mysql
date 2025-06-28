package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    Order getOrderWithDetails(Integer id);

    List<Order> getOrdersByUserId(Integer userId);
}