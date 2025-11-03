package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 游戏评论表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    /**
     * 游戏ID
     */
    private Integer gameId;

    /**
     * 发表评论的用户ID
     */
    private Integer userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论审核状态
     */
    private String status;

    /**
     * 发表时间
     */
    private LocalDateTime createdAt;

    /**
     * 审核管理员ID
     */
    private Integer reviewedByAdminId;
}
