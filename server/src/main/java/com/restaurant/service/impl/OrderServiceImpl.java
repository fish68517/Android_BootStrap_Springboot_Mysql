package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.OrderDetailMapper;
import com.restaurant.mapper.OrderMapper;
import com.restaurant.mapper.ReviewMapper;
import com.restaurant.model.Order;
import com.restaurant.model.OrderDetail;
import com.restaurant.model.Review;
import com.restaurant.service.DishService;
import com.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Order getOrderWithDetails(Integer orderId) {
        // 1. 获取订单基本信息
        Order order = orderMapper.getById(orderId);
        if (order == null) {
            return null;
        }

        // 2. 获取订单项（使用专门的订单项表）
       // List<OrderDetail> orderItems = orderDetailMapper.selectDetailsByOrderId(orderId);

        order.setOrderItems(null);

        // 3. 获取订单评价
        Review review = reviewMapper.getByOrderId(orderId);
        order.setReview(review);

        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderMapper.getOrderByUserId(userId);
        // 为每个订单加载其评价信息
        // 注意: 此处存在 N+1 查询问题，如果订单量大，性能会受影响。
        // 优化方案可以是批量查询所有订单的评价，然后在内存中进行匹配。
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                Review review = reviewMapper.getByOrderId(order.getOrderId());
                order.setReview(review);
            }
        }
        return orders;
    }
} 