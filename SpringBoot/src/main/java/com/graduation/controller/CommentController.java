package com.graduation.controller;

import com.graduation.entity.Comments;
import com.graduation.entity.Users;
import com.graduation.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论控制器
 * 处理评论发表、删除、点赞等请求
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表评论
     * 
     * @param gameId 游戏ID
     * @param content 评论内容
     * @param session HTTP会话
     * @return 重定向到游戏详情页
     */
    @PostMapping("/post")
    public String postComment(@RequestParam Integer gameId,
                             @RequestParam String content,
                             HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        commentService.postComment(gameId, currentUser.getUserId(), content);
        return "redirect:/game/detail/" + gameId;
    }

    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @param session HTTP会话
     * @return 重定向到上一页
     */
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Integer id,
                               HttpSession session,
                               @RequestHeader(value = "Referer", required = false) String referer) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        commentService.deleteComment(id, currentUser.getUserId(), currentUser.getRole());
        
        // 返回到上一页，如果没有referer则返回首页
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }

    /**
     * 点赞评论（AJAX请求）
     * 
     * @param id 评论ID
     * @param session HTTP会话
     * @return JSON响应
     */
    @PostMapping("/like/{id}")
    @ResponseBody
    public Map<String, Object> likeComment(@PathVariable Integer id,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return response;
        }

        try {
            commentService.likeComment(id, currentUser.getUserId());
            int likeCount = commentService.getLikeCount(id);
            
            response.put("success", true);
            response.put("likeCount", likeCount);
            response.put("message", "点赞成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "点赞失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 取消点赞（AJAX请求）
     * 
     * @param id 评论ID
     * @param session HTTP会话
     * @return JSON响应
     */
    @PostMapping("/unlike/{id}")
    @ResponseBody
    public Map<String, Object> unlikeComment(@PathVariable Integer id,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return response;
        }

        try {
            commentService.unlikeComment(id, currentUser.getUserId());
            int likeCount = commentService.getLikeCount(id);
            
            response.put("success", true);
            response.put("likeCount", likeCount);
            response.put("message", "取消点赞成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消点赞失败：" + e.getMessage());
        }
        
        return response;
    }
}
