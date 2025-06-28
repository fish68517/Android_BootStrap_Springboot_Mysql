package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.CartMapper;
import com.restaurant.model.CartItem;
import com.restaurant.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, CartItem> implements CartService {
    
    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<CartItem> getUserCart(Integer userId) {
        return cartMapper.selectCartItemsWithDish(userId);
    }
    
    @Override
    @Transactional
    public boolean updateCartQuantity(Integer userId, Integer cartItemId, Integer quantity,double price) {
        if (quantity <= 0) {
            // 如果数量小于等于0，则删除该购物车项
            return cartMapper.deleteCartItem(userId, cartItemId) > 0;
        }
        return cartMapper.updateCartItemQuantity(userId, cartItemId, quantity,price) > 0;
    }
    
    @Override
    @Transactional
    public boolean removeFromCart(Integer userId, Integer cartItemId) {
        return cartMapper.deleteCartItem(userId, cartItemId) > 0;
    }
    
    @Override
    @Transactional
    public boolean clearCart(Integer userId) {
        return cartMapper.clearUserCart(userId) >= 0;
    }
    
    @Override
    @Transactional
    public boolean addToCart(CartItem cartItem) {
        // 检查购物车是否已存在该商品
        CartItem existingItem = cartMapper.selectCartItemByUserIdAndDishId(
            cartItem.getUserId(), cartItem.getDishId());

        return cartMapper.insert(cartItem) > 0;
    }

    public boolean updateQuantity(Integer cartItemId, Integer quantity,double price) {
        CartItem cartItem = getById(cartItemId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            // 重新计算总价
            cartItem.setTotalPrice(price);
            return updateById(cartItem);
        }
        return false;
    }
    
    @Override
    public int getCartItemCount(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return (int) count(queryWrapper);
    }
} 