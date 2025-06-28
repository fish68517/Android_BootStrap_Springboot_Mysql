package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order getById(Integer orderId);

    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    List<Order> getOrderByUserId(Integer userId);

    @Insert("INSERT INTO orders (user_id, store_id, order_no, total_amount, delivery_fee, status, " +
            "order_type,delivery_address, payment_method, created_at, cart_items) " +
            "VALUES (#{userId}, #{storeId}, #{orderNo}, #{totalAmount}, #{deliveryFee}, #{status}," +
            "#{orderType},#{deliveryAddress},#{paymentMethod},NOW(),#{cartItems})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")  // keyProperty 是 Order 对象中的属性
    int save(Order order);

    @Select("SELECT * FROM orders WHERE store_id = #{storeId}")
    List<Order> getOrdersByStoreId(Integer storeId);
}