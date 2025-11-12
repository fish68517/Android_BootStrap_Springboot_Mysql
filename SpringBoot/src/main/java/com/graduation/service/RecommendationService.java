package com.graduation.service;

import com.graduation.entity.Recommendations;
import java.util.List;

/**
 * 推荐服务接口
 * 提供管理员创建推荐和获取用户推荐列表功能
 */
public interface RecommendationService {
    
    /**
     * 管理员创建推荐
     * @param adminId 管理员ID
     * @param userId 被推荐的用户ID
     * @param gameId 被推荐的游戏ID
     * @param reason 推荐理由
     * @return 创建的推荐对象
     */
    Recommendations createRecommendation(Integer adminId, Integer userId, Integer gameId, String reason);
    
    /**
     * 获取用户的推荐列表
     * @param userId 用户ID
     * @return 推荐列表
     */
    List<Recommendations> getUserRecommendations(Integer userId);
    
    /**
     * 获取所有推荐列表（管理员使用）
     * @return 所有推荐列表
     */
    List<Recommendations> getAllRecommendations();
}
