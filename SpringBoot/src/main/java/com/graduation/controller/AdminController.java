package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.entity.Games;
import com.graduation.entity.Recommendations;
import com.graduation.entity.Users;
import com.graduation.service.GamesService;
import com.graduation.service.RecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin") // 所有管理员相关的 URL 都在 /admin 路径下
public class AdminController {

    @Autowired
    private GamesService gamesService; // 注入 GamesService

    @Autowired
    private RecommendationsService recommendationsService; // 注入 RecommendationsService

    /**
     * 显示管理员登录页面
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin/login";
    }

    /**
     * 处理管理员登录表单提交
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        System.out.println("登录尝试: " + username);
        // ... (您的登录验证逻辑) ...
        return "redirect:/admin/dashboard";
    }

    /**
     * 1. 显示管理员主页 (已更新)
     * - 增加 Model model 来传递数据
     * - 增加 @RequestParam 来接收查询参数
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, @RequestParam(value = "query", required = false) String query) {

        // ---------------------------------
        // 1. 加载游戏 (用于 Banner 或 搜索结果)
        // ---------------------------------
        List<Games> games;
        boolean isSearchResult = false;

        if (StringUtils.hasText(query)) {
            // A. 如果有查询词 (query)，则执行搜索
            LambdaQueryWrapper<Games> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Games::getTitle, query); // 模糊查询标题
            games = gamesService.list(wrapper);
            isSearchResult = true;
        } else {
            // B. 如果没有查询词，则加载所有游戏作为 Banner
            games = gamesService.list();
        }

        model.addAttribute("games", games);
        model.addAttribute("isSearchResult", isSearchResult);
        model.addAttribute("query", query); // 回显查询词

        // ---------------------------------
        // 2. 加载游戏推荐 (页面下方)
        // ---------------------------------
        List<Recommendations> recommendations = recommendationsService.list();

        // 为了在前端显示推荐游戏的名字，我们把所有游戏查出来放进一个 Map
        // (这是简单方案，不考虑性能)
        Map<Integer, Games> gameMap = gamesService.list().stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));

        model.addAttribute("recommendations", recommendations);
        model.addAttribute("gameMap", gameMap);

        return "admin/dashboard"; // 返回 dashboard 页面
    }


    // ---------------------------------
    //  【【【步骤 1：新增的方法】】】
    // ---------------------------------
    /**
     * 6. 【新功能】显示游戏详情页面
     * @param gameId 路径变量, 从 URL 中获取
     * @param model Spring Model
     * @return 模板路径 "admin/game_detail"
     */
    @GetMapping("/game/{id}")
    public String showGameDetail(@PathVariable("id") Integer gameId, Model model) {

        // 1. 查询游戏
        Games game = gamesService.getById(gameId);
        if (game == null) {
            // 如果游戏不存在, 重定向回主页
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("game", game);

        // 2. 查询发布者信息
        Users publisher = usersService.getById(game.getPublisherId());
        model.addAttribute("publisher", publisher != null ? publisher : new Users()); // 传递一个空对象防止 Thymeleaf 报 Null 错

        // 3. 查询审核员信息 (如果有)
        if (game.getReviewedByAdminId() != null) {
            Users reviewer = usersService.getById(game.getReviewedByAdminId());
            model.addAttribute("reviewer", reviewer);
        }

        // 4. 处理 "更多图片"
        // 将 "url1,url2,url3" 字符串
        List<String> otherImages = new ArrayList<>();
        if (StringUtils.hasText(game.getOtherImageUrls())) {
            // 按逗号分割成一个 List
            otherImages = Arrays.asList(game.getOtherImageUrls().split(","));
        }
        model.addAttribute("otherImages", otherImages);

        // 5. 返回新创建的详情页模板
        return "admin/game_detail";
    }

    /**
     * 2. 显示排行榜页面
     */
    @GetMapping("/leaderboard")
    public String showLeaderboard() {
        return "admin/leaderboard";
    }

    /**
     * 3. 显示收藏页面
     */
    @GetMapping("/favorites")
    public String showFavorites() {
        return "admin/favorites";
    }

    /**
     * 4. 显示点赞页面
     */
    @GetMapping("/likes")
    public String showLikes() {
        return "admin/likes";
    }

    /**
     * 5. 显示个人中心页面
     */
    @GetMapping("/profile")
    public String showProfile() {
        return "admin/profile";
    }
}