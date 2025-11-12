package com.graduation.service.impl;

import com.graduation.entity.Games;
import com.graduation.entity.Recommendations;
import com.graduation.entity.Users;
import com.graduation.exception.GameNotFoundException;
import com.graduation.exception.UserNotFoundException;
import com.graduation.mapper.GamesMapper;
import com.graduation.mapper.RecommendationsMapper;
import com.graduation.mapper.UsersMapper;
import com.graduation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐服务实现类
 */
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    
    private final RecommendationsMapper recommendationsMapper;
    private final UsersMapper usersMapper;
    private final GamesMapper gamesMapper;
    
    @Override
    @Transactional
    public Recommendations createRecommendation(Integer adminId, Integer userId, Integer gameId, String reason) {
        // 验证用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }
        
        // 验证游戏是否存在且已审核通过
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        if (!"approved".equals(game.getStatus())) {
            throw new GameNotFoundException("只能推荐已审核通过的游戏");
        }
        
        // 创建推荐记录
        Recommendations recommendation = new Recommendations();
        recommendation.setAdminId(adminId);
        recommendation.setUserId(userId);
        recommendation.setGameId(gameId);
        recommendation.setReason(reason);
        recommendation.setCreatedAt(LocalDateTime.now());
        
        recommendationsMapper.insert(recommendation);
        return recommendation;
    }
    
    @Override
    public List<Recommendations> getUserRecommendations(Integer userId) {
        return recommendationsMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Recommendations> getAllRecommendations() {
        return recommendationsMapper.selectList(null);
    }
}
