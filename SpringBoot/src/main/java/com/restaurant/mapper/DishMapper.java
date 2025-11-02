package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.model.Dish;
import com.restaurant.model.Store;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    List<Dish> getAllDishes();

    @Select("SELECT * FROM dishes WHERE category_id = #{categoryId}")
    @Results({
            @Result(property = "dishOptions", column = "dish_options", javaType = String.class),
            @Result(
                    property = "store",
                    column = "store_id",
                    javaType = Store.class,
                    one = @One(select = "getStoreByStoreId")
            )
    })
    List<Dish> getByCategoryId(Integer categoryId);

    @Select("SELECT * FROM stores WHERE store_id = #{storeId}")
    Store getStoreByStoreId(Integer storeId);

    @Select("SELECT * FROM dishes WHERE dish_id = #{dishId}")
    Dish selectById(Integer dishId);
}