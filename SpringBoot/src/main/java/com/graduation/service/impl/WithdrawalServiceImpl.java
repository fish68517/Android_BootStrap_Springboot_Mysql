package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.entity.Games;
import com.graduation.entity.WithdrawalRequests;
import com.graduation.exception.GameNotFoundException;
import com.graduation.exception.InvalidStatusException;
import com.graduation.exception.UnauthorizedException;
import com.graduation.mapper.*;
import com.graduation.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 撤回申请服务实现类
 */
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {
    
    private final WithdrawalRequestsMapper withdrawalRequestsMapper;
    private final GamesMapper gamesMapper;
    private final CommentsMapper commentsMapper;
    private final CommentLikesMapper commentLikesMapper;
    private final UserFavoritesMapper userFavoritesMapper;
    private final RecommendationsMapper recommendationsMapper;
    
    @Override
    @Transactional
    public WithdrawalRequests submitWithdrawalRequest(Integer gameId, Integer publisherId, String reason) {
        // 验证游戏是否存在
        Games game = gamesMapper.selectById(gameId);
        if (game == null) {
            throw new GameNotFoundException("游戏不存在");
        }
        
        // 验证权限：只有发布者本人可以申请撤回
        if (!game.getPublisherId().equals(publisherId)) {
            throw new UnauthorizedException("您没有权限申请撤回此游戏");
        }
        
        // 检查是否已有待审核的撤回申请
        QueryWrapper<WithdrawalRequests> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("game_id", gameId)
                   .eq("status", "pending");
        Long count = withdrawalRequestsMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new InvalidStatusException("该游戏已有待审核的撤回申请");
        }
        
        // 创建撤回申请
        WithdrawalRequests request = new WithdrawalRequests();
        request.setGameId(gameId);
        request.setPublisherId(publisherId);
        request.setReason(reason);
        request.setStatus("pending");
        request.setRequestedAt(LocalDateTime.now());
        
        withdrawalRequestsMapper.insert(request);
        return request;
    }
    
    @Override
    public List<WithdrawalRequests> getPendingWithdrawalRequests() {
        return withdrawalRequestsMapper.selectPendingRequests();
    }
    
    @Override
    @Transactional
    public void reviewWithdrawalRequest(Integer requestId, String status, Integer adminId) {
        // 验证撤回申请是否存在
        WithdrawalRequests request = withdrawalRequestsMapper.selectById(requestId);
        if (request == null) {
            throw new GameNotFoundException("撤回申请不存在");
        }
        
        // 验证状态值
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new InvalidStatusException("无效的审核状态");
        }
        
        // 更新撤回申请状态
        request.setStatus(status);
        request.setReviewedByAdminId(adminId);
        request.setReviewedAt(LocalDateTime.now());
        withdrawalRequestsMapper.updateById(request);
        
        // 如果审核通过，删除游戏及所有关联数据
        if ("approved".equals(status)) {
            deleteGameAndRelatedData(request.getGameId());
        }
    }
    
    /**
     * 删除游戏及所有关联数据
     * @param gameId 游戏ID
     */
    private void deleteGameAndRelatedData(Integer gameId) {
        // 1. 获取该游戏的所有评论ID
        QueryWrapper<com.graduation.entity.Comments> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("game_id", gameId);
        List<com.graduation.entity.Comments> comments = commentsMapper.selectList(commentQueryWrapper);
        
        // 2. 删除所有评论的点赞记录
        for (com.graduation.entity.Comments comment : comments) {
            QueryWrapper<com.graduation.entity.CommentLikes> likeQueryWrapper = new QueryWrapper<>();
            likeQueryWrapper.eq("comment_id", comment.getCommentId());
            commentLikesMapper.delete(likeQueryWrapper);
        }
        
        // 3. 删除所有评论
        QueryWrapper<com.graduation.entity.Comments> deleteCommentWrapper = new QueryWrapper<>();
        deleteCommentWrapper.eq("game_id", gameId);
        commentsMapper.delete(deleteCommentWrapper);
        
        // 4. 删除所有收藏记录
        QueryWrapper<com.graduation.entity.UserFavorites> favoriteQueryWrapper = new QueryWrapper<>();
        favoriteQueryWrapper.eq("game_id", gameId);
        userFavoritesMapper.delete(favoriteQueryWrapper);
        
        // 5. 删除所有推荐记录
        QueryWrapper<com.graduation.entity.Recommendations> recommendationQueryWrapper = new QueryWrapper<>();
        recommendationQueryWrapper.eq("game_id", gameId);
        recommendationsMapper.delete(recommendationQueryWrapper);
        
        // 6. 删除游戏本身
        gamesMapper.deleteById(gameId);
    }
    
    @Override
    public List<WithdrawalRequests> getWithdrawalRequestsByGameId(Integer gameId) {
        return withdrawalRequestsMapper.selectByGameId(gameId);
    }
    
    @Override
    public List<WithdrawalRequests> getWithdrawalRequestsByPublisherId(Integer publisherId) {
        return withdrawalRequestsMapper.selectByPublisherId(publisherId);
    }
}
