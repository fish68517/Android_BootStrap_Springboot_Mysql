package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.model.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT * FROM reviews WHERE review_id = #{reviewId}")
    Review getById(Integer reviewId);

    @Select("SELECT * FROM reviews WHERE order_id = #{orderId}")
    Review getByOrderId(Integer orderId);

    @Select("SELECT * FROM reviews WHERE user_id = #{userId}")
    List<Review> getByUserId(Integer userId);

    @Select("SELECT * FROM reviews WHERE store_id = #{storeId}")
    List<Review> getByStoreId(Integer storeId);
} 