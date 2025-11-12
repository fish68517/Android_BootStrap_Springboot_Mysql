package com.graduation.mapper;

import com.graduation.entity.Comments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 游戏评论表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface CommentsMapper extends BaseMapper<Comments> {

    /**
     * 根据游戏ID和状态查询评论列表
     */
    @Select("SELECT * FROM comments WHERE game_id = #{gameId} AND status = #{status} ORDER BY created_at DESC")
    List<Comments> selectByGameIdAndStatus(@Param("gameId") Integer gameId, @Param("status") String status);

    /**
     * 查询所有待审核的评论
     */
    @Select("SELECT * FROM comments WHERE status = 'pending' ORDER BY created_at ASC")
    List<Comments> selectPendingComments();
}
