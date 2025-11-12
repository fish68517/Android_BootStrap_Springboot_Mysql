package com.graduation.controller;

import com.graduation.entity.Games;
import com.graduation.entity.Users;
import com.graduation.service.FavoriteService;
import com.graduation.util.ResponseUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@Controller
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏（AJAX请求）
     */
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addFavorite(@RequestParam Integer gameId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseUtil.error("请先登录");
        }

        try {
            favoriteService.favoriteGame(currentUser.getUserId(), gameId);
            return ResponseUtil.success("收藏成功");
        } catch (Exception e) {
            return ResponseUtil.error("收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏（AJAX请求）
     */
    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeFavorite(@RequestParam Integer gameId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseUtil.error("请先登录");
        }

        try {
            favoriteService.unfavoriteGame(currentUser.getUserId(), gameId);
            return ResponseUtil.success("取消收藏成功");
        } catch (Exception e) {
            return ResponseUtil.error("取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 收藏列表页面
     */
    @GetMapping("/list")
    public String listFavorites(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        List<Games> favorites = favoriteService.getUserFavorites(currentUser.getUserId());
        model.addAttribute("favorites", favorites);
        model.addAttribute("currentUser", currentUser);

        return "favorite/list";
    }
}
