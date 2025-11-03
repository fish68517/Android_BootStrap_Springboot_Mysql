package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 评论点赞表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
@TableName("comment_likes")
public class CommentLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞用户ID
     */
    //@TableId("user_id")
    private Integer userId;

    /**
     * 被点赞的评论ID
     */
    // @TableId("comment_id")
    private Integer commentId;

    /**
     * 点赞时间
     */
    private LocalDateTime likedAt;
}
