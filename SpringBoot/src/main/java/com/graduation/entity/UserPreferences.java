package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 用户偏好关联表 (复合主键: userId + preferenceId)
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("user_preferences")
public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID (复合主键之一)
     */
    private Integer userId;

    /**
     * 偏好ID (复合主键之一)
     */
    private Integer preferenceId;
}
