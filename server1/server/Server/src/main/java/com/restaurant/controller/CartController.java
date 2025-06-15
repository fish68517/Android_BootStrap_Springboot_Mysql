package com.restaurant.controller;

import com.restaurant.model.CartItem;
import com.restaurant.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping("/user/{userId}")
    public List<CartItem> getUserCart(@PathVariable Integer userId) {
        return cartService.getUserCart(userId);
    }
    
    @PostMapping("/add")
    public boolean addToCart(@RequestBody CartItem cartItem) {
        System.out.println("添加到购物车："+cartItem);
        // 设置创建时间和更新时间
        Date now = new Date();
        cartItem.setCreatedAt(now);
        cartItem.setUpdatedAt(now);
        return cartService.addToCart(cartItem);
    }
    
    @PutMapping("/quantity")
    public boolean updateQuantity(@RequestParam Integer userId,
                                @RequestParam Integer cartItemId,
                                    @RequestParam Integer quantity,
                                  @RequestParam double totalPrice) {
        return cartService.updateCartQuantity(userId, cartItemId,quantity,totalPrice);
    }
    
    @DeleteMapping("/remove/{cartItemId}/{userId}")
    public boolean removeFromCart(@PathVariable Integer cartItemId, @PathVariable Integer userId) {
        return cartService.removeFromCart(userId,cartItemId);
    }
    
    @DeleteMapping("/clear")
    public boolean clearUserCart(@RequestParam Integer userId) {
        return cartService.clearCart(userId);
    }
    
    @GetMapping("/count/{userId}")
    public int getCartItemCount(@PathVariable Integer userId) {
        return cartService.getCartItemCount(userId);
    }


} 