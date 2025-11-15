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
    public ResponseEntity<ApiResponse> addFavorite(@RequestBody FavoriteRequestDTO request) {

        favoriteService.favoriteGame(request.getUserId(), request.getGameId());
        return ResponseEntity.ok(ApiResponse.success("收藏成功"));
    }

    /**
     * 取消收藏 (API)
     *
     */
    @PostMapping("/remove")
    public ResponseEntity<ApiResponse> removeFavorite(@RequestBody FavoriteRequestDTO request) {

        favoriteService.unfavoriteGame(request.getUserId(), request.getGameId());
        return ResponseEntity.ok(ApiResponse.success("取消收藏成功"));
    }

    /**
     * 获取我的收藏列表 (API)
     *
     */
    @GetMapping("/list")
    public ResponseEntity<List<Games>> getMyFavorites(@RequestParam Integer userId) {

        List<Games> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    /**
     * 检查游戏是否已收藏 (API)
     *
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(@RequestParam Integer gameId, @RequestParam Integer userId) {
        boolean isFavorited = favoriteService.isFavorited(userId, gameId);
        return ResponseEntity.ok(isFavorited);
    }
}