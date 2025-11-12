package com.graduation.service;

import com.graduation.entity.Games;
import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 收藏游戏
     * @param userId 用户ID
     * @param gameId 游戏ID
     */
    void favoriteGame(Integer userId, Integer gameId);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param gameId 游戏ID
     */
    void unfavoriteGame(Integer userId, Integer gameId);
    
    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @return 收藏的游戏列表
     */
    List<Games> getUserFavorites(Integer userId);
    
    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param gameId 游戏ID
     * @return true表示已收藏，false表示未收藏
     */
    boolean isFavorited(Integer userId, Integer gameId);
    
    /**
     * 获取所有收藏记录
     * 用于管理员查看
     * @return 所有收藏记录列表
     */
    List<com.graduation.entity.UserFavorites> getAllFavorites();
    
    /**
     * 管理员删除收藏记录
     * @param userId 用户ID
     * @param gameId 游戏ID
     */
    void removeFavorite(Integer userId, Integer gameId);
}
