package com.graduation.controller;

import com.graduation.dto.ApiResponse;
import com.graduation.dto.CommentPostDTO;
import com.graduation.entity.Users;
import com.graduation.exception.UnauthorizedException;
import com.graduation.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentApiController {

    @Autowired
    private CommentService commentService; //

    /**
     * 发表评论 (API)
     *
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse> postComment(@RequestBody CommentPostDTO request, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        commentService.postComment(request.getGameId(), currentUser.getUserId(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success("评论成功"));
    }

    /**
     * 删除评论 (API)
     *
     */
/*    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Integer commentId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        // 业务逻辑（检查是否为作者或管理员）已在 service 中处理
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }*/

    /**
     * 点赞评论 (API)
     *
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse> likeComment(@PathVariable("id") Integer commentId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        commentService.likeComment(commentId, currentUser.getUserId());
        int likeCount = commentService.getLikeCount(commentId);

        return ResponseEntity.ok(ApiResponse.success("点赞成功").put("likeCount", likeCount));
    }

    /**
     * 取消点赞评论 (API)
     *
     */
    @PostMapping("/unlike/{id}")
    public ResponseEntity<ApiResponse> unlikeComment(@PathVariable("id") Integer commentId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        commentService.unlikeComment(commentId, currentUser.getUserId());
        int likeCount = commentService.getLikeCount(commentId);

        return ResponseEntity.ok(ApiResponse.success("取消点赞成功").put("likeCount", likeCount));
    }
}