package com.graduation.service;

import com.graduation.entity.Comments;
import com.graduation.entity.Users;
import com.graduation.exception.UnauthorizedException;
import com.graduation.exception.UserNotFoundException;
import com.graduation.mapper.CommentsMapper;
import com.graduation.mapper.CommentLikesMapper;
import com.graduation.entity.CommentLikes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务类
 * 处理评论发布、审核、点赞等业务逻辑
 */
@Service
public class CommentService {

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentLikesMapper commentLikesMapper;

    /**
     * 发表评论
     * 状态设为pending等待审核
     * 
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 创建的评论对象
     */
    @Transactional
    public Comments postComment(Integer gameId, Integer userId, String content) {
        Comments comment = new Comments();
        comment.setGameId(gameId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setStatus("pending");
        comment.setCreatedAt(LocalDateTime.now());
        
        commentsMapper.insert(comment);
        return comment;
    }

    /**
     * 删除评论
     * 验证权限：只有评论作者或管理员可以删除
     * 
     * @param commentId 评论ID
     * @param currentUserId 当前用户ID
     * @param currentUserRole 当前用户角色
     * @throws UnauthorizedException 无权限删除
     */
    @Transactional
    public void deleteComment(Integer commentId, Integer currentUserId, String currentUserRole) {
        Comments comment = commentsMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        // 验证权限：评论作者或管理员可以删除
        if (!comment.getUserId().equals(currentUserId) && !"admin".equals(currentUserRole)) {
            throw new UnauthorizedException("无权限删除此评论");
        }

        commentsMapper.deleteById(commentId);
    }

    /**
     * 管理员审核评论
     * 
     * @param commentId 评论ID
     * @param status 审核状态 (approved/rejected)
     * @param adminId 管理员ID
     */
    @Transactional
    public void reviewComment(Integer commentId, String status, Integer adminId) {
        Comments comment = commentsMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new IllegalArgumentException("无效的审核状态");
        }

        comment.setStatus(status);
        comment.setReviewedByAdminId(adminId);
        commentsMapper.updateById(comment);
    }

    /**
     * 获取游戏的已审核评论
     * 
     * @param gameId 游戏ID
     * @return 已审核的评论列表
     */
    public List<Comments> getApprovedComments(Integer gameId) {
        return commentsMapper.selectByGameIdAndStatus(gameId, "approved");
    }

    /**
     * 获取所有待审核评论
     * 
     * @return 待审核评论列表
     */
    public List<Comments> getPendingComments() {
        return commentsMapper.selectPendingComments();
    }

    /**
     * 点赞评论
     * 插入点赞记录
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Transactional
    public void likeComment(Integer commentId, Integer userId) {
        // 检查是否已点赞
        int count = commentLikesMapper.checkUserLiked(userId, commentId);
        if (count > 0) {
            // 已点赞，不重复插入
            return;
        }

        CommentLikes like = new CommentLikes();
        like.setUserId(userId);
        like.setCommentId(commentId);
        like.setLikedAt(LocalDateTime.now());
        
        commentLikesMapper.insert(like);
    }

    /**
     * 取消点赞
     * 删除点赞记录
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Transactional
    public void unlikeComment(Integer commentId, Integer userId) {
        commentLikesMapper.deleteByUserIdAndCommentId(userId, commentId);
    }

    /**
     * 获取评论点赞数
     * 
     * @param commentId 评论ID
     * @return 点赞数
     */
    public int getLikeCount(Integer commentId) {
        return commentLikesMapper.countByCommentId(commentId);
    }

    /**
     * 检查用户是否已点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    public boolean isLikedByUser(Integer commentId, Integer userId) {
        return commentLikesMapper.checkUserLiked(userId, commentId) > 0;
    }
}
