package com.graduation.service;

import com.graduation.dto.GameSubmitDTO;
import com.graduation.entity.Games;
import java.util.List;

/**
 * 游戏服务接口
 * 提供游戏发布、管理、审核等功能
 */
public interface GameService {
    
    /**
     * 发布游戏
     * @param dto 游戏提交数据
     * @param publisherId 发布者ID
     * @return 创建的游戏对象
     */
    Games publishGame(GameSubmitDTO dto, Integer publisherId);
    
    /**
     * 修改游戏信息
     * @param gameId 游戏ID
     * @param dto 游戏提交数据
     * @param publisherId 发布者ID（用于权限验证）
     */
    void updateGame(Integer gameId, GameSubmitDTO dto, Integer publisherId);
    
    /**
     * 根据状态查询游戏列表
     * @param status 游戏状态 (pending, approved, rejected)
     * @return 游戏列表
     */
    List<Games> getGamesByStatus(String status);
    
    /**
     * 搜索游戏（按标题模糊查询）
     * @param keyword 搜索关键词
     * @return 游戏列表
     */
    List<Games> searchGames(String keyword);
    
    /**
     * 获取游戏详情
     * @param gameId 游戏ID
     * @return 游戏对象
     */
    Games getGameDetail(Integer gameId);
    
    /**
     * 管理员审核游戏
     * @param gameId 游戏ID
     * @param status 审核状态 (approved, rejected)
     * @param adminId 管理员ID
     */
    void reviewGame(Integer gameId, String status, Integer adminId);
    
    /**
     * 根据发布者ID查询游戏列表
     * @param publisherId 发布者ID
     * @return 游戏列表
     */
    List<Games> getGamesByPublisherId(Integer publisherId);
    
    /**
     * 获取所有已审核通过的游戏
     * @return 游戏列表
     */
    List<Games> getApprovedGames();
}
