package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.dto.GameSubmitDTO;
import com.graduation.entity.Games;
import com.graduation.exception.GameNotFoundException;
import com.graduation.exception.InvalidStatusException;
import com.graduation.exception.UnauthorizedException;
import com.graduation.mapper.GamesMapper;
import com.graduation.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 游戏服务实现类
 */
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    
    private final GamesMapper gamesMapper;
    
    @Override
    @Transactional
    public Games publishGame(GameSubmitDTO dto, Integer publisherId) {
        Games game = new Games();
        game.setPublisherId(publisherId);
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setCoverImageUrl(dto.getCoverImageUrl());
        game.setOtherImageUrls(dto.getOtherImageUrls());
        game.setStatus("pending");
        game.setSubmittedAt(LocalDateTime.now());
        
        gamesMapper.insert(game);
        return game;
    }
    
    @Override
    @Transactional
    public void updateGame(Integer gameId, GameSubmitDTO dto, Integer publisherId) {
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        
        // 验证权限：只有发布者本人可以修改
        if (!game.getPublisherId().equals(publisherId)) {
            throw new UnauthorizedException("您没有权限修改此游戏");
        }
        
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setCoverImageUrl(dto.getCoverImageUrl());
        game.setOtherImageUrls(dto.getOtherImageUrls());
        
        gamesMapper.updateById(game);
    }
    
    @Override
    public List<Games> getGamesByStatus(String status) {
        return gamesMapper.selectByStatus(status);
    }
    
    @Override
    public List<Games> searchGames(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getApprovedGames();
        }
        return gamesMapper.searchByTitle(keyword);
    }
    
    @Override
    public Games getGameDetail(Integer gameId) {
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        return game;
    }
    
    @Override
    @Transactional
    public void reviewGame(Integer gameId, String status, Integer adminId) {
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        
        // 验证状态值
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new InvalidStatusException("无效的审核状态");
        }
        
        game.setStatus(status);
        game.setReviewedByAdminId(adminId);
        game.setReviewedAt(LocalDateTime.now());
        
        gamesMapper.updateById(game);
    }
    
    @Override
    public List<Games> getGamesByPublisherId(Integer publisherId) {
        return gamesMapper.selectByPublisherId(publisherId);
    }
    
    @Override
    public List<Games> getApprovedGames() {
        return gamesMapper.selectByStatus("approved");
    }
}
