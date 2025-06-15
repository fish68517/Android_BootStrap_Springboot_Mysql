package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.CartItem;

import java.util.List;

public interface CartService extends IService<CartItem> {
    // 获取用户购物车列表
    List<CartItem> getUserCart(Integer userId);
    
    // 更新购物车商品数量
    boolean updateCartQuantity(Integer userId, Integer cartItemId, Integer quantity,double price);
    
    // 从购物车移除商品
    boolean removeFromCart(Integer userId, Integer cartItemId);
    
    // 清空购物车
    boolean clearCart(Integer userId);
    
    // 添加商品到购物车
    boolean addToCart(CartItem cartItem);
    
    // 获取购物车商品总数
    int getCartItemCount(Integer userId);
} 