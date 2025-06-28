package com.restaurant.controller;

import com.restaurant.model.OrderDetail;
import com.restaurant.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {
    
    @Autowired
    private OrderDetailService orderDetailService;
    
    // 获取订单详情列表
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetails(orderId));
    }
    
    // 批量添加订单详情
    @PostMapping("/batch")
    public ResponseEntity<Boolean> addDetails(@RequestBody List<OrderDetail> details) {
        return ResponseEntity.ok(orderDetailService.saveBatch(details));
    }
    
    // 更新订单详情
    @PutMapping("/{detailId}")
    public ResponseEntity<Boolean> updateDetail(@PathVariable Integer detailId, 
                                             @RequestBody OrderDetail detail) {
        detail.setDetailId(detailId);
        return ResponseEntity.ok(orderDetailService.updateDetail(detail));
    }
    
    // 删除订单详情
    @DeleteMapping("/{detailId}")
    public ResponseEntity<Boolean> removeDetail(@PathVariable Integer detailId) {
        return ResponseEntity.ok(orderDetailService.removeDetail(detailId));
    }
} 