package com.graduation.mapper;

import com.graduation.entity.UserPreferences;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 用户偏好关联表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface UserPreferencesMapper extends BaseMapper<UserPreferences> {

    /**
     * 查询用户的所有偏好
     */
    @Select("SELECT * FROM user_preferences WHERE user_id = #{userId}")
    List<UserPreferences> selectByUserId(@Param("userId") Integer userId);

    /**
     * 删除用户的所有偏好
     */
    @Delete("DELETE FROM user_preferences WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Integer userId);
}
