package com.graduation.controller;

import com.graduation.entity.Games;
import com.graduation.entity.Recommendations;
import com.graduation.entity.Users;
import com.graduation.service.GameService;
import com.graduation.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    @Autowired
    private GameService gameService;

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");

        // 获取热门游戏（已审核通过的游戏，限制显示数量）
        List<Games> games = gameService.getGamesByStatus("approved");
        // 限制首页显示的游戏数量为8个
        if (games.size() > 8) {
            games = games.subList(0, 8);
        }
        model.addAttribute("games", games);

        // 如果用户已登录，获取推荐游戏
        if (currentUser != null) {
            List<Recommendations> recommendations = recommendationService.getUserRecommendations(currentUser.getUserId());
            model.addAttribute("recommendations", recommendations);

            // 创建推荐游戏的Map，方便在模板中使用
            if (!recommendations.isEmpty()) {
                Map<Integer, Games> recommendedGameMap = new HashMap<>();
                for (Recommendations rec : recommendations) {
                    Games game = gameService.getGameDetail(rec.getGameId());
                    if (game != null && "approved".equals(game.getStatus())) {
                        recommendedGameMap.put(rec.getGameId(), game);
                    }
                }
                model.addAttribute("recommendedGameMap", recommendedGameMap);
            }
        }

        return "index";
    }
}
