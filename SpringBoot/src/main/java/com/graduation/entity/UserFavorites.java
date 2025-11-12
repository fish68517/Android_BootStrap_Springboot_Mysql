package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 用户收藏表 (复合主键: userId + gameId)
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("user_favorites")
public class UserFavorites implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID (复合主键之一)
     */
    private Integer userId;

    /**
     * 游戏ID (复合主键之一)
     */
    private Integer gameId;

    /**
     * 收藏时间
     */
    private LocalDateTime favoritedAt;
}
