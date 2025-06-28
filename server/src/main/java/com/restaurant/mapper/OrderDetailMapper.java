package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.config.JsonTypeHandler;
import com.restaurant.model.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    
    // 查询订单的所有详情（包含菜品信息）
    @Select("SELECT " +
            "od.detail_id, od.order_id, od.dish_id, od.quantity, od.price, " +
            "od.selected_options, od.created_at, " +
            "d.dish_id as dish_dish_id, d.category_id, d.store_id, d.dish_name, " +
            "d.price as dish_price, d.description, d.image, d.status, " +
            "d.created_at as dish_created_at, d.dish_options " +
            "FROM order_details od " +
            "LEFT JOIN dishes d ON od.dish_id = d.dish_id " +
            "WHERE od.order_id = #{orderId}")
    @Results({
            @Result(property = "detailId", column = "detail_id"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "dishId", column = "dish_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "price", column = "price"),
            @Result(property = "selectedOptions", column = "selected_options",
                    typeHandler = JsonTypeHandler.class),
            @Result(property = "createdAt", column = "created_at"),
            
            // 菜品信息映射
            @Result(property = "dish.dishId", column = "dish_dish_id"),
            @Result(property = "dish.categoryId", column = "category_id"),
            @Result(property = "dish.storeId", column = "store_id"),
            @Result(property = "dish.dishName", column = "dish_name"),
            @Result(property = "dish.price", column = "dish_price"),
            @Result(property = "dish.description", column = "description"),
            @Result(property = "dish.image", column = "image"),
            @Result(property = "dish.status", column = "status"),
            @Result(property = "dish.createdAt", column = "dish_created_at"),
            @Result(property = "dish.dishOptions", column = "dish_options")
    })
    List<OrderDetail> selectDetailsByOrderId(Integer orderId);
    
    // 批量插入订单详情
    @Insert("<script>" +
            "INSERT INTO order_details (order_id, dish_id, quantity, price, selected_options) VALUES " +
            "<foreach collection='details' item='detail' separator=','>" +
            "(#{detail.orderId}, #{detail.dishId}, #{detail.quantity}, #{detail.price}, " +
            "#{detail.selectedOptions,typeHandler=com.restaurant.config.JsonTypeHandler})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("details") List<OrderDetail> details);
} 