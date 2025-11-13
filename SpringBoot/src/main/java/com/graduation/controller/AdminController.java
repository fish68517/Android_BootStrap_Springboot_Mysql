package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.entity.Games;
import com.graduation.entity.Recommendations;
import com.graduation.entity.Users;
import com.graduation.entity.WithdrawalRequests;
import com.graduation.service.GamesService;
import com.graduation.service.RecommendationsService;
import com.graduation.service.UsersService;
import com.graduation.service.WithdrawalService;
import jakarta.servlet.http.HttpSession;
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
    private UsersService usersService; // 注入 GamesService

    @Autowired
    private RecommendationsService recommendationsService; // 注入 RecommendationsService
    
    @Autowired
    private com.graduation.service.RecommendationService recommendationService; // 注入新的 RecommendationService
    
    @Autowired
    private WithdrawalService withdrawalService; // 注入 WithdrawalService
    
    @Autowired
    private com.graduation.service.UserService userService; // 注入 UserService
    
    @Autowired
    private com.graduation.service.CommentService commentService; // 注入 CommentService
    
    @Autowired
    private com.graduation.service.GameService gameService; // 注入 GameService
    
    @Autowired
    private com.graduation.service.FavoriteService favoriteService; // 注入 FavoriteService

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
     * 显示个人资料页面
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // 检查是否登录
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/admin/login";
        }

        // 获取最新的用户信息
        Users user = userService.getUserById(currentUser.getUserId());
        model.addAttribute("user", user);
        return "admin/profile";
    }
    
    /**
     * 6. 显示推荐管理页面
     */
    @GetMapping("/recommendations")
    public String showRecommendations(Model model) {
        // 获取所有推荐列表
        List<Recommendations> recommendations = recommendationService.getAllRecommendations();
        
        // 获取所有用户和游戏，用于显示详细信息
        List<Users> users = usersService.list();
        List<Games> games = gamesService.list();
        
        // 创建 Map 方便前端查询
        Map<Integer, Users> userMap = users.stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        Map<Integer, Games> gameMap = games.stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));
        
        model.addAttribute("recommendations", recommendations);
        model.addAttribute("userMap", userMap);
        model.addAttribute("gameMap", gameMap);
        
        // 获取所有用户和已审核通过的游戏，用于创建新推荐
        model.addAttribute("users", users);
        
        // 只显示已审核通过的游戏
        List<Games> approvedGames = games.stream()
                .filter(g -> "approved".equals(g.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("approvedGames", approvedGames);
        
        return "admin/recommendations";
    }
    
    /**
     * 7. 处理创建推荐请求
     */
    @PostMapping("/recommendations/create")
    public String createRecommendation(@RequestParam("userId") Integer userId,
                                      @RequestParam("gameId") Integer gameId,
                                      @RequestParam("reason") String reason,
                                      @SessionAttribute(value = "currentUser", required = false) Users currentUser,
                                      Model model) {
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                model.addAttribute("error", "您没有权限执行此操作");
                return "redirect:/admin/recommendations";
            }
            
            // 创建推荐
            recommendationService.createRecommendation(currentUser.getUserId(), userId, gameId, reason);
            
            model.addAttribute("success", "推荐创建成功");
        } catch (Exception e) {
            model.addAttribute("error", "创建推荐失败: " + e.getMessage());
        }
        
        return "redirect:/admin/recommendations";
    }
    
    /**
     * 8. 显示待审核游戏列表页面
     */
    @GetMapping("/games/pending")
    public String showPendingGames(Model model) {
        // 获取所有待审核的游戏
        List<Games> pendingGames = gameService.getGamesByStatus("pending");
        
        // 获取发布者信息
        Map<Integer, Users> publisherMap = usersService.list().stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        
        model.addAttribute("pendingGames", pendingGames);
        model.addAttribute("publisherMap", publisherMap);
        
        return "admin/review-games";
    }
    
    /**
     * 9. 处理游戏审核
     */
    @PostMapping("/games/review")
    public String reviewGame(@RequestParam("gameId") Integer gameId,
                            @RequestParam("status") String status,
                            @SessionAttribute(value = "currentUser", required = false) Users currentUser,
                            Model model) {
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                model.addAttribute("error", "您没有权限执行此操作");
                return "redirect:/admin/games/pending";
            }
            
            // 审核游戏
            gameService.reviewGame(gameId, status, currentUser.getUserId());
            
            if ("approved".equals(status)) {
                model.addAttribute("success", "游戏已通过审核");
            } else {
                model.addAttribute("success", "游戏已被拒绝");
            }
        } catch (Exception e) {
            model.addAttribute("error", "审核失败: " + e.getMessage());
        }
        
        return "redirect:/admin/games/pending";
    }
    
    /**
     * 10. 显示待审核评论列表页面
     */
    @GetMapping("/comments/pending")
    public String showPendingComments(Model model) {
        // 获取所有待审核的评论
        List<com.graduation.entity.Comments> pendingComments = commentService.getPendingComments();
        
        // 获取相关的游戏和用户信息
        Map<Integer, Games> gameMap = gamesService.list().stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));
        Map<Integer, Users> userMap = usersService.list().stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        
        model.addAttribute("pendingComments", pendingComments);
        model.addAttribute("gameMap", gameMap);
        model.addAttribute("userMap", userMap);
        
        return "admin/review-comments";
    }
    
    /**
     * 11. 处理评论审核
     */
    @PostMapping("/comments/review")
    public String reviewComment(@RequestParam("commentId") Integer commentId,
                               @RequestParam("status") String status,
                               @SessionAttribute(value = "currentUser", required = false) Users currentUser,
                               Model model) {
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                model.addAttribute("error", "您没有权限执行此操作");
                return "redirect:/admin/comments/pending";
            }
            
            // 审核评论
            commentService.reviewComment(commentId, status, currentUser.getUserId());
            
            if ("approved".equals(status)) {
                model.addAttribute("success", "评论已通过审核");
            } else {
                model.addAttribute("success", "评论已被拒绝");
            }
        } catch (Exception e) {
            model.addAttribute("error", "审核失败: " + e.getMessage());
        }
        
        return "redirect:/admin/comments/pending";
    }
    
    /**
     * 12. 显示用户管理页面
     */
    @GetMapping("/users")
    public String showUsers(Model model) {
        // 获取所有用户
        List<Users> users = userService.getAllUsers();
        
        model.addAttribute("users", users);
        
        return "admin/users";
    }
    
    /**
     * 13. 显示待审核撤回申请列表页面
     */
    @GetMapping("/withdrawals/pending")
    public String showPendingWithdrawals(Model model) {
        // 获取所有待审核的撤回申请
        List<WithdrawalRequests> withdrawalRequests = withdrawalService.getPendingWithdrawalRequests();
        
        // 获取相关的游戏和用户信息
        Map<Integer, Games> gameMap = gamesService.list().stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));
        Map<Integer, Users> userMap = usersService.list().stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        
        model.addAttribute("withdrawalRequests", withdrawalRequests);
        model.addAttribute("gameMap", gameMap);
        model.addAttribute("userMap", userMap);
        
        return "admin/review-withdrawals";
    }
    
    /**
     * 14. 处理撤回申请审核
     */
    @PostMapping("/withdrawals/review")
    public String reviewWithdrawal(@RequestParam("requestId") Integer requestId,
                                   @RequestParam("status") String status,
                                   @SessionAttribute(value = "currentUser", required = false) Users currentUser,
                                   Model model) {
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                model.addAttribute("error", "您没有权限执行此操作");
                return "redirect:/admin/withdrawals/pending";
            }
            
            // 审核撤回申请
            withdrawalService.reviewWithdrawalRequest(requestId, status, currentUser.getUserId());
            
            if ("approved".equals(status)) {
                model.addAttribute("success", "撤回申请已通过，游戏及相关数据已删除");
            } else {
                model.addAttribute("success", "撤回申请已拒绝");
            }
        } catch (Exception e) {
            model.addAttribute("error", "审核失败: " + e.getMessage());
        }
        
        return "redirect:/admin/withdrawals/pending";
    }
    
    /**
     * 15. 显示所有收藏管理页面
     */
    @GetMapping("/manage-favorites")
    public String showManageFavorites(Model model) {
        // 获取所有收藏记录
        List<com.graduation.entity.UserFavorites> allFavorites = favoriteService.getAllFavorites();
        
        // 获取所有用户和游戏信息
        Map<Integer, Users> userMap = usersService.list().stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        Map<Integer, Games> gameMap = gamesService.list().stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));
        
        model.addAttribute("favorites", allFavorites);
        model.addAttribute("userMap", userMap);
        model.addAttribute("gameMap", gameMap);
        
        return "admin/manage-favorites";
    }
    
    /**
     * 16. 删除收藏记录
     */
    @PostMapping("/favorites/delete")
    @ResponseBody
    public Map<String, Object> deleteFavorite(@RequestParam("userId") Integer userId,
                                              @RequestParam("gameId") Integer gameId,
                                              @SessionAttribute(value = "currentUser", required = false) Users currentUser) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                result.put("success", false);
                result.put("message", "您没有权限执行此操作");
                return result;
            }
            
            // 删除收藏记录
            favoriteService.removeFavorite(userId, gameId);
            
            result.put("success", true);
            result.put("message", "收藏记录已删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 17. 显示所有点赞管理页面
     */
    @GetMapping("/manage-likes")
    public String showManageLikes(Model model) {
        // 获取所有点赞记录
        List<com.graduation.entity.CommentLikes> allLikes = commentService.getAllLikes();
        
        // 获取所有用户、评论和游戏信息
        Map<Integer, Users> userMap = usersService.list().stream()
                .collect(Collectors.toMap(Users::getUserId, u -> u));
        Map<Integer, com.graduation.entity.Comments> commentMap = 
            commentService.getAllComments().stream()
                .collect(Collectors.toMap(com.graduation.entity.Comments::getCommentId, c -> c));
        Map<Integer, Games> gameMap = gamesService.list().stream()
                .collect(Collectors.toMap(Games::getGameId, g -> g));
        
        model.addAttribute("likes", allLikes);
        model.addAttribute("userMap", userMap);
        model.addAttribute("commentMap", commentMap);
        model.addAttribute("gameMap", gameMap);
        
        return "admin/manage-likes";
    }
    
    /**
     * 18. 删除点赞记录
     */
    @PostMapping("/likes/delete")
    @ResponseBody
    public Map<String, Object> deleteLike(@RequestParam("userId") Integer userId,
                                          @RequestParam("commentId") Integer commentId,
                                          @SessionAttribute(value = "currentUser", required = false) Users currentUser) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // 验证管理员身份
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                result.put("success", false);
                result.put("message", "您没有权限执行此操作");
                return result;
            }
            
            // 删除点赞记录
            commentService.removeLike(commentId, userId);
            
            result.put("success", true);
            result.put("message", "点赞记录已删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }
}