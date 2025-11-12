package com.graduation.mapper;

import com.graduation.entity.Recommendations;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 管理员推荐表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface RecommendationsMapper extends BaseMapper<Recommendations> {

    /**
     * 查询用户的所有推荐
     */
    @Select("SELECT * FROM recommendations WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Recommendations> selectByUserId(@Param("userId") Integer userId);

    /**
     * 查询游戏的所有推荐
     */
    @Select("SELECT * FROM recommendations WHERE game_id = #{gameId} ORDER BY created_at DESC")
    List<Recommendations> selectByGameId(@Param("gameId") Integer gameId);
}
