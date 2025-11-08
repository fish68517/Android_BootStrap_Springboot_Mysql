package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 游戏信息表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Getter
@Setter
@Entity
@Table(name = "games")
public class Games implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "game_id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 审核状态
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




    private String coverImageUrl;

    private String otherImageUrls;
}
