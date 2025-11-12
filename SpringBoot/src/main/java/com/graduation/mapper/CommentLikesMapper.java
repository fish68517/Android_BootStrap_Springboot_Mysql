package com.graduation.mapper;

import com.graduation.entity.CommentLikes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 评论点赞表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface CommentLikesMapper extends BaseMapper<CommentLikes> {

    /**
     * 查询评论的点赞数
     */
    @Select("SELECT COUNT(*) FROM comment_likes WHERE comment_id = #{commentId}")
    int countByCommentId(@Param("commentId") Integer commentId);

    /**
     * 检查用户是否已点赞评论
     */
    @Select("SELECT COUNT(*) FROM comment_likes WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int checkUserLiked(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    /**
     * 删除点赞记录
     */
    @Delete("DELETE FROM comment_likes WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int deleteByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
}
