package com.graduation.mapper;

import com.graduation.entity.UserFavorites;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 用户收藏表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface UserFavoritesMapper extends BaseMapper<UserFavorites> {

    /**
     * 查询用户的所有收藏
     */
    @Select("SELECT * FROM user_favorites WHERE user_id = #{userId} ORDER BY favorited_at DESC")
    List<UserFavorites> selectByUserId(@Param("userId") Integer userId);

    /**
     * 检查用户是否已收藏游戏
     */
    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId} AND game_id = #{gameId}")
    int checkUserFavorited(@Param("userId") Integer userId, @Param("gameId") Integer gameId);

    /**
     * 删除收藏记录
     */
    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND game_id = #{gameId}")
    int deleteByUserIdAndGameId(@Param("userId") Integer userId, @Param("gameId") Integer gameId);
}
