package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 偏好定义表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("preferences")
public class Preferences implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "preference_id", type = IdType.AUTO)
    private Integer preferenceId;

    /**
     * 偏好名称 (如: RPG, 策略, 射击, 模拟, 休闲)
     */
    private String preferenceName;
}
