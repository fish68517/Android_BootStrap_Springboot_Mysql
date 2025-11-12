package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 评论点赞表 (复合主键: userId + commentId)
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("comment_likes")
public class CommentLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞用户ID (复合主键之一)
     */
    private Integer userId;

    /**
     * 被点赞的评论ID (复合主键之一)
     */
    private Integer commentId;

    /**
     * 点赞时间
     */
    private LocalDateTime likedAt;
}
