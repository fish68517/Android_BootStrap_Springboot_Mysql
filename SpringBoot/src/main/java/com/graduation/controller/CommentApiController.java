package com.graduation.controller;

import com.graduation.dto.ApiResponse;
import com.graduation.dto.CommentPostDTO;
import com.graduation.entity.Comments;
import com.graduation.entity.Users;
import com.graduation.exception.UnauthorizedException;
import com.graduation.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentApiController {

    @Autowired
    private CommentService commentService; //


    /**
     * 根据 Game ID 获取评论列表
     * GET /api/comment/list/{gameId}
     */
    @GetMapping("/list/{gameId}")
    public ResponseEntity<List<Comments>> getCommentsByGameId(@PathVariable Integer gameId) {
        List<Comments> comments = commentService.getCommentsByGameId(gameId);
        return ResponseEntity.ok(comments); // 返回 200 OK 和评论列表
    }

    /**
     * 根据 User ID 获取评论列表
     * GET /api/comment/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comments>> getCommentsByUserId(@PathVariable Integer userId) {
        List<Comments> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }


    /**
     * 发表评论 (API)
     *
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse> postComment(@RequestBody CommentPostDTO request) {


        commentService.postComment(request.getGameId(), request.getUserId(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success("评论成功"));
    }

/*
    *//**
     * 创建一条新评论 (增)
     * POST /api/comment
     *//*
    @PostMapping
    public ResponseEntity<Comments> createComment(@RequestBody Comments comment) {
        Comments createdComment = commentService.createComment(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED); // 返回 201 Created
    }*/

    /**
     * 获取单条评论详情 (查)
     * GET /api/comment/{id}
     */
/*    @GetMapping("/{id}")
    public ResponseEntity<Comments> getCommentById(@PathVariable Integer id) {
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok) // 如果找到，返回 200 OK
                .orElse(ResponseEntity.notFound().build()); // 如果没找到，返回 404 Not Found
    }*/

  /*  *//**
     * 更新一条评论 (改)
     * PUT /api/comment/{id}
     *//*
    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComment(@PathVariable Integer id, @RequestBody Comments commentDetails) {
        return commentService.updateComment(id, commentDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/
/*
    *//**
     * 删除一条评论 (删)
     * DELETE /api/comment/{id}
     *//*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        if (commentService.deleteComment(id)) {
            return ResponseEntity.noContent().build(); // 删除成功，返回 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 资源不存在，返回 404 Not Found
        }
    }*/

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
    public ResponseEntity<ApiResponse> likeComment(@PathVariable("id") Integer commentId,@RequestParam Integer userId) {

        commentService.likeComment(commentId, userId);
        int likeCount = commentService.getLikeCount(commentId);

        return ResponseEntity.ok(ApiResponse.success("点赞成功").put("likeCount", likeCount));
    }

    /**
     * 取消点赞评论 (API)
     *
     */
    @PostMapping("/unlike/{id}")
    public ResponseEntity<ApiResponse> unlikeComment(@PathVariable("id") Integer commentId,@RequestParam Integer userId) {

        commentService.unlikeComment(commentId, userId);
        int likeCount = commentService.getLikeCount(commentId);

        return ResponseEntity.ok(ApiResponse.success("取消点赞成功").put("likeCount", likeCount));
    }

    //// 获取 userId 和 commentId 的点赞状态
    //    @GET("/api/comment/like/status/{userId}/{commentId}")
    //    Call<Boolean> getLikeStatus(@Path("userId") int userId, @Path("commentId") int commentId);
    @GetMapping("/like/status/{userId}/{commentId}")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable("userId") Integer userId, @PathVariable("commentId") Integer commentId) {
        boolean isLiked = commentService.isLikedByUser(commentId, userId);
        return ResponseEntity.ok(isLiked);
    }
}