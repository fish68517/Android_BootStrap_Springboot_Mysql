package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.OrderDetailMapper;
import com.restaurant.model.OrderDetail;
import com.restaurant.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> 
        implements OrderDetailService {
    
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    
    @Override
    public List<OrderDetail> getOrderDetails(Integer orderId) {
        return orderDetailMapper.selectDetailsByOrderId(orderId);
    }
    
    @Override
    @Transactional
    public boolean saveBatch(List<OrderDetail> details) {
        return orderDetailMapper.insertBatch(details) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateDetail(OrderDetail detail) {
        return updateById(detail);
    }
    
    @Override
    @Transactional
    public boolean removeDetail(Integer detailId) {
        return removeById(detailId);
    }
} 