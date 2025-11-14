package com.gameplatform.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 收藏状态管理工具类
 * 用于在应用内同步收藏状态变化
 * Requirements: 4.1, 4.2, 4.5
 */
public class FavoriteManager {
    
    private static FavoriteManager instance;
    private Set<String> favoriteGameIds;
    private Set<FavoriteStatusListener> listeners;
    
    public interface FavoriteStatusListener {
        void onFavoriteStatusChanged(String gameId, boolean isFavorited);
    }
    
    private FavoriteManager() {
        favoriteGameIds = new HashSet<>();
        listeners = new HashSet<>();
    }
    
    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }
    
    /**
     * 添加收藏状态监听器
     * @param listener 监听器
     */
    public void addListener(FavoriteStatusListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 移除收藏状态监听器
     * @param listener 监听器
     */
    public void removeListener(FavoriteStatusListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 更新游戏收藏状态
     * @param gameId 游戏ID
     * @param isFavorited 是否收藏
     */
    public void updateFavoriteStatus(String gameId, boolean isFavorited) {
        if (isFavorited) {
            favoriteGameIds.add(gameId);
        } else {
            favoriteGameIds.remove(gameId);
        }
        
        // 通知所有监听器
        for (FavoriteStatusListener listener : listeners) {
            listener.onFavoriteStatusChanged(gameId, isFavorited);
        }
    }
    
    /**
     * 检查游戏是否已收藏
     * @param gameId 游戏ID
     * @return 是否已收藏
     */
    public boolean isFavorited(String gameId) {
        return favoriteGameIds.contains(gameId);
    }
    
    /**
     * 设置收藏游戏ID集合
     * @param gameIds 游戏ID集合
     */
    public void setFavoriteGameIds(Set<String> gameIds) {
        favoriteGameIds.clear();
        if (gameIds != null) {
            favoriteGameIds.addAll(gameIds);
        }
    }
    
    /**
     * 获取收藏游戏ID集合
     * @return 游戏ID集合
     */
    public Set<String> getFavoriteGameIds() {
        return new HashSet<>(favoriteGameIds);
    }
    
    /**
     * 清空所有数据
     */
    public void clear() {
        favoriteGameIds.clear();
        listeners.clear();
    }
}