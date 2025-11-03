package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户收藏表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
@TableName("user_favorites")
public class UserFavorites implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    //@TableId("user_id")
    private Integer userId;

    /**
     * 游戏ID
     */
    // @TableId("game_id")
    private Integer gameId;

    /**
     * 收藏时间
     */
    private LocalDateTime favoritedAt;
}
