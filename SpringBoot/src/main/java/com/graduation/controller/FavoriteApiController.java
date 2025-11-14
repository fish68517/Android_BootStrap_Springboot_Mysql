package com.graduation.controller;

import com.graduation.dto.ApiResponse;
import com.graduation.dto.FavoriteRequestDTO;
import com.graduation.entity.Games;
import com.graduation.entity.Users;
import com.graduation.exception.UnauthorizedException;
import com.graduation.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteApiController {

    @Autowired
    private FavoriteService favoriteService; //

    /**
     * 添加收藏 (API)
     *
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addFavorite(@RequestBody FavoriteRequestDTO request, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        favoriteService.favoriteGame(currentUser.getUserId(), request.getGameId());
        return ResponseEntity.ok(ApiResponse.success("收藏成功"));
    }

    /**
     * 取消收藏 (API)
     *
     */
    @PostMapping("/remove")
    public ResponseEntity<ApiResponse> removeFavorite(@RequestBody FavoriteRequestDTO request, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        favoriteService.unfavoriteGame(currentUser.getUserId(), request.getGameId());
        return ResponseEntity.ok(ApiResponse.success("取消收藏成功"));
    }

    /**
     * 获取我的收藏列表 (API)
     *
     */
    @GetMapping("/list")
    public ResponseEntity<List<Games>> getMyFavorites(HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }
        List<Games> favorites = favoriteService.getUserFavorites(currentUser.getUserId());
        return ResponseEntity.ok(favorites);
    }
}