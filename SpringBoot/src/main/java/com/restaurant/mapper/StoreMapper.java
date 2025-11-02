package com.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.model.DishCategory;
import com.restaurant.model.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    @Select("SELECT DISTINCT dc.* FROM dish_categories dc " +
            "INNER JOIN dishes d ON dc.category_id = d.category_id " +
            "WHERE d.store_id = #{storeId}")
    // 修改 storeId ，如果为 null 则返回所有分类
    List<DishCategory> listCategoriesByStoreId(Integer storeId);

    @Select("SELECT DISTINCT dc.* FROM dish_categories dc " +
            "INNER JOIN dishes d ON dc.category_id = d.category_id ")     // 修改 storeId ，如果为 null 则返回所有分类
    List<DishCategory> listCategories();
}