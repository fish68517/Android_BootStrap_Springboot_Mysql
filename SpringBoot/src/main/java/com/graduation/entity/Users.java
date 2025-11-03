package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 账号（手机或邮箱）
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String passwordHash;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户分类
     */
    private String role;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;
}
