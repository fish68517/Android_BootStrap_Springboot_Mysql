package com.restaurant.controller;

import com.restaurant.mapper.OrderMapper;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.service.DishService;
import com.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DishService dishService;

    @GetMapping
    public List<Order> listAll() {
        return orderService.list();
    }

    @GetMapping("/user/{userId}")
    public List<Order> list( @PathVariable Integer userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return orders;
    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Order>> getOrdersByStoreId(@PathVariable Integer storeId) {
        try {
            // Assuming orderService has a method to fetch orders by store ID.
            // This needs to be implemented in the service and mapper layers.
            List<Order> orders = orderMapper.getOrdersByStoreId(storeId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderWithDetails(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching order: " + e.getMessage());
        }
    }

    @PostMapping
    public Integer save(@RequestBody Order order) {
        if (order.getOrderNo() == null) {
            // 生成订单号 通过随机数生成 生成10位的订单号
            order.setOrderNo(UUID.randomUUID().toString().substring(0, 10));
        }
        Integer orderId = orderMapper.save(order);
        System.out.println("order id: " + orderId);
        return orderId;
    }

    @PutMapping
    public boolean update(@RequestBody Order order) {
        return orderService.updateById(order);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return orderService.removeById(id);
    }
} 