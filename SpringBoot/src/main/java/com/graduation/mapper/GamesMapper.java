package com.graduation.mapper;

import com.graduation.entity.Games;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 游戏信息表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface GamesMapper extends BaseMapper<Games> {

    /**
     * 根据状态查询游戏列表
     */
    @Select("SELECT * FROM games WHERE status = #{status} ORDER BY submitted_at DESC")
    List<Games> selectByStatus(@Param("status") String status);

    /**
     * 根据发布者ID查询游戏列表
     */
    @Select("SELECT * FROM games WHERE publisher_id = #{publisherId} ORDER BY submitted_at DESC")
    List<Games> selectByPublisherId(@Param("publisherId") Integer publisherId);

    /**
     * 根据标题关键词搜索游戏
     */
    @Select("SELECT * FROM games WHERE title LIKE CONCAT('%', #{keyword}, '%') AND status = 'approved' ORDER BY submitted_at DESC")
    List<Games> searchByTitle(@Param("keyword") String keyword);
}
