package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.OrderDetail;
import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    // 获取订单详情列表
    List<OrderDetail> getOrderDetails(Integer orderId);
    
    // 批量添加订单详情
    boolean saveBatch(List<OrderDetail> details);
    
    // 更新订单详情
    boolean updateDetail(OrderDetail detail);
    
    // 删除订单详情
    boolean removeDetail(Integer detailId);
} 