package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 游戏信息表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("games")
public class Games implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "game_id", type = IdType.AUTO)
    private Integer gameId;

    /**
     * 发布者ID (关联用户表)
     */
    private Integer publisherId;

    /**
     * 游戏名称
     */
    private String title;

    /**
     * 游戏信息描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 其他图片URLs (逗号分隔)
     */
    private String otherImageUrls;

    /**
     * 审核状态 (pending, approved, rejected)
     */
    private String status;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 审核管理员ID
     */
    private Integer reviewedByAdminId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
}
