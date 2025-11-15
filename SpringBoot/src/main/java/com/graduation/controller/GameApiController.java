package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.entity.Games;
import com.graduation.entity.Recommendations;
import com.graduation.entity.Users;
import com.graduation.service.GameService;
import com.graduation.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameApiController {

    @Autowired
    private GameService gameService; //

    @Autowired
    private RecommendationService recommendationService; //

    /**
     * 获取所有已批准的游戏列表 (API)
     *
     */
    @GetMapping("/list")
    public ResponseEntity<List<Games>> getAllGames(
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "query", defaultValue = "") String query) {

        List<Games> games = gameService.getApprovedGames();

        if (!category.isEmpty()) {
            Iterator<Games> iterator = games.iterator();
            while (iterator.hasNext()) {
                Games game = iterator.next();
                if (!game.getCategory().equals(category)) {
                    iterator.remove();
                }
            }
        }

        if (!query.isEmpty()) {
            Iterator<Games> iterator = games.iterator();
            while (iterator.hasNext()) {
                Games game = iterator.next();
                if (!game.getTitle().contains(query)) {
                    iterator.remove();
                }
            }
        }

        return ResponseEntity.ok(games);
    }


    /**
     * 搜索游戏 (API)
     *
     */
    @GetMapping("/search")
    public ResponseEntity<List<Games>> searchGames(@RequestParam("query") String query) {
/*        LambdaQueryWrapper<Games> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Games::getStatus, "approved") // 只能搜到已批准的
                .like(Games::getTitle, query);     // 模糊查询标题*/
        List<Games> games = gameService.searchGames(query);
        return ResponseEntity.ok(games);
    }

    /**
     * 获取游戏详情 (API)
     *
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<Games> getGameDetail(@PathVariable("id") Integer id) {
        Games game = gameService.getGameDetail(id);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(game);
    }

    /**
     * 获取我发布的游戏 (API)
     *
     */
/*    @GetMapping("/my-games")
    public ResponseEntity<List<Games>> getMyGames(HttpSession session) {
        Users currentUser = (Users) session.getAttribute("currentUser");
     *//*   if (currentUser == null) {
            throw new UnauthorizedException("用户未登录");
        }*//*

        LambdaQueryWrapper<Games> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Games::getPublisherId, currentUser.getUserId());
        List<Games> myGames = gameService.list(wrapper);
        return ResponseEntity.ok(myGames);
    }*/

    /**
     * 获取首页推荐 (API)
     *
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<Recommendations>> getRecommendations() {
        // (您可能需要根据用户偏好进行推荐，这里简化为获取所有推荐)
        List<Recommendations> recommendations = recommendationService.getAllRecommendations();
        return ResponseEntity.ok(recommendations);
    }
}