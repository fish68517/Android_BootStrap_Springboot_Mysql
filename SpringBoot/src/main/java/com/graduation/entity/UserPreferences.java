package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户偏好关联表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
@TableName("user_preferences")
public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    // @TableId("user_id")
    private Integer userId;

    /**
     * 偏好ID
     */
    // @TableId("preference_id")
    private Integer preferenceId;
}
