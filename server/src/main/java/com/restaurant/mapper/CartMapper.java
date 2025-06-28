package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.config.JsonTypeHandler;
import com.restaurant.model.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<CartItem> {
    // 查询用户购物车列表（包含菜品信息）
    @Select("SELECT " +
            "c.id, c.dish_id, c.user_id, c.quantity, c.selected_options, " +
            "c.total_price, c.cart_type, c.created_at as cart_created_at, c.updated_at, " +
            "d.dish_id as dish_dish_id, d.category_id, d.store_id, d.dish_name, " +
            "d.price, d.description, d.image, d.status, " +
            "d.created_at as dish_created_at, d.dish_options " +
            "FROM cart_items c " +
            "LEFT JOIN dishes d ON c.dish_id = d.dish_id " +
            "WHERE c.user_id = #{userId}")
    @Results({
            // CartItem 的字段映射
            @Result(property = "id", column = "id"),
            @Result(property = "dishId", column = "dish_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "selectedOptions", column = "selected_options",
                    typeHandler = JsonTypeHandler.class),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "cartType", column = "cart_type"),
            @Result(property = "createdAt", column = "cart_created_at"),
            @Result(property = "updatedAt", column = "updated_at"),

            // Dish 对象的字段映射
            @Result(property = "dish.dishId", column = "dish_dish_id"),
            @Result(property = "dish.categoryId", column = "category_id"),
            @Result(property = "dish.storeId", column = "store_id"),
            @Result(property = "dish.dishName", column = "dish_name"),
            @Result(property = "dish.price", column = "price"),
            @Result(property = "dish.description", column = "description"),
            @Result(property = "dish.image", column = "image"),
            @Result(property = "dish.status", column = "status"),
            @Result(property = "dish.createdAt", column = "dish_created_at"),
            @Result(property = "dish.dishOptions", column = "dish_options")
    })
    List<CartItem> selectCartItemsWithDish(Integer userId);

    // 更新购物车商品数量
    @Update("UPDATE cart_items SET quantity = #{quantity},total_price = #{totalPrice}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND id = #{cartItemId}")
    int updateCartItemQuantity(@Param("userId") Integer userId, 
                             @Param("cartItemId") Integer cartItemId,
                             @Param("quantity") Integer quantity,
                             @Param("totalPrice") double totalPrice);

    // 删除购物车商品
    @Delete("DELETE FROM cart_items WHERE user_id = #{userId} AND id = #{cartItemId}")
    int deleteCartItem(@Param("userId") Integer userId, @Param("cartItemId") Integer cartItemId);

    // 清空用户购物车
    @Delete("DELETE FROM cart_items WHERE user_id = #{userId}")
    int clearUserCart(@Param("userId") Integer userId);

    // 根据用户ID和菜品ID查询购物车项
    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND dish_id = #{dishId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "dishId", column = "dish_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "selectedOptions", column = "selected_options",
                    typeHandler = JsonTypeHandler.class),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "cartType", column = "cart_type"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    CartItem selectCartItemByUserIdAndDishId(@Param("userId") Integer userId, 
                                           @Param("dishId") Integer dishId);
} 