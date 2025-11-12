package com.graduation.service;

import com.graduation.entity.WithdrawalRequests;
import java.util.List;

/**
 * 撤回申请服务接口
 * 提供游戏撤回申请的提交、审核等功能
 */
public interface WithdrawalService {
    
    /**
     * 提交撤回申请
     * @param gameId 游戏ID
     * @param publisherId 发布者ID
     * @param reason 撤回理由
     * @return 创建的撤回申请对象
     */
    WithdrawalRequests submitWithdrawalRequest(Integer gameId, Integer publisherId, String reason);
    
    /**
     * 获取所有待审核的撤回申请
     * @return 撤回申请列表
     */
    List<WithdrawalRequests> getPendingWithdrawalRequests();
    
    /**
     * 管理员审核撤回申请
     * @param requestId 申请ID
     * @param status 审核状态 (approved, rejected)
     * @param adminId 管理员ID
     */
    void reviewWithdrawalRequest(Integer requestId, String status, Integer adminId);
    
    /**
     * 根据游戏ID查询撤回申请
     * @param gameId 游戏ID
     * @return 撤回申请列表
     */
    List<WithdrawalRequests> getWithdrawalRequestsByGameId(Integer gameId);
    
    /**
     * 根据发布者ID查询撤回申请
     * @param publisherId 发布者ID
     * @return 撤回申请列表
     */
    List<WithdrawalRequests> getWithdrawalRequestsByPublisherId(Integer publisherId);
}
