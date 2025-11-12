package com.graduation.service.impl;

import com.graduation.entity.Games;
import com.graduation.entity.UserFavorites;
import com.graduation.exception.GameNotFoundException;
import com.graduation.mapper.GamesMapper;
import com.graduation.mapper.UserFavoritesMapper;
import com.graduation.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoritesMapper userFavoritesMapper;
    private final GamesMapper gamesMapper;

    @Override
    @Transactional
    public void favoriteGame(Integer userId, Integer gameId) {
        // 检查游戏是否存在
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        
        // 检查是否已收藏
        if (isFavorited(userId, gameId)) {
            return; // 已收藏，直接返回
        }
        
        // 创建收藏记录
        UserFavorites favorite = new UserFavorites();
        favorite.setUserId(userId);
        favorite.setGameId(gameId);
        favorite.setFavoritedAt(LocalDateTime.now());
        
        userFavoritesMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void unfavoriteGame(Integer userId, Integer gameId) {
        userFavoritesMapper.deleteByUserIdAndGameId(userId, gameId);
    }

    @Override
    public List<Games> getUserFavorites(Integer userId) {
        // 获取用户的所有收藏记录
        List<UserFavorites> favorites = userFavoritesMapper.selectByUserId(userId);
        
        // 根据收藏记录获取游戏信息
        List<Games> games = new ArrayList<>();
        for (UserFavorites favorite : favorites) {
            Games game = gamesMapper.selectById(favorite.getGameId());
            if (game != null) {
                games.add(game);
            }
        }
        
        return games;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer gameId) {
        int count = userFavoritesMapper.checkUserFavorited(userId, gameId);
        return count > 0;
    }
    
    @Override
    public List<UserFavorites> getAllFavorites() {
        return userFavoritesMapper.selectList(null);
    }
    
    @Override
    @Transactional
    public void removeFavorite(Integer userId, Integer gameId) {
        userFavoritesMapper.deleteByUserIdAndGameId(userId, gameId);
    }
}
