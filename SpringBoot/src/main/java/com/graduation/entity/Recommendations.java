package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 管理员推荐表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
public class Recommendations implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "recommendation_id", type = IdType.AUTO)
    private Integer recommendationId;

    /**
     * 操作推荐的管理员ID
     */
    private Integer adminId;

    /**
     * 被推荐的用户ID
     */
    private Integer userId;

    /**
     * 被推荐的游戏ID
     */
    private Integer gameId;

    /**
     * 推荐理由
     */
    private String reason;

    private LocalDateTime createdAt;
}
