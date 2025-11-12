package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 游戏撤回审核表
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Data
@TableName("withdrawal_requests")
public class WithdrawalRequests implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "request_id", type = IdType.AUTO)
    private Integer requestId;

    /**
     * 申请撤回的游戏ID
     */
    private Integer gameId;

    /**
     * 申请的发布者ID
     */
    private Integer publisherId;

    /**
     * 撤回理由
     */
    private String reason;

    /**
     * 撤回审核状态 (pending, approved, rejected)
     */
    private String status;

    /**
     * 申请时间
     */
    private LocalDateTime requestedAt;

    /**
     * 审核管理员ID
     */
    private Integer reviewedByAdminId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
}
