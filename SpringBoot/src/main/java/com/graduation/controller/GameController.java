package com.graduation.controller;

import com.graduation.dto.GameSubmitDTO;
import com.graduation.entity.Games;
import com.graduation.entity.Users;
import com.graduation.entity.Comments;
import com.graduation.service.GameService;
import com.graduation.service.CommentService;
import com.graduation.service.FavoriteService;
import com.graduation.service.WithdrawalService;
import com.graduation.util.FileUploadUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏控制器
 * 处理游戏相关的请求
 */
@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    
    private final GameService gameService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;
    private final com.graduation.service.RecommendationService recommendationService;
    private final WithdrawalService withdrawalService;
    private final FileUploadUtil fileUploadUtil;
    
    /**
     * 显示游戏列表页面（支持搜索）
     */
    @GetMapping("/list")
    public String listGames(@RequestParam(required = false) String query, Model model, HttpSession session) {
        List<Games> games;
        if (query != null && !query.trim().isEmpty()) {
            games = gameService.searchGames(query);
            model.addAttribute("query", query);
        } else {
            games = gameService.getApprovedGames();
        }
        model.addAttribute("games", games);
        
        // 如果用户已登录，添加收藏状态信息和推荐列表
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser != null) {
            Map<Integer, Boolean> favoriteStatus = new HashMap<>();
            for (Games game : games) {
                favoriteStatus.put(game.getGameId(), favoriteService.isFavorited(currentUser.getUserId(), game.getGameId()));
            }
            model.addAttribute("favoriteStatus", favoriteStatus);
            
            // 获取用户的推荐列表
            List<com.graduation.entity.Recommendations> recommendations = recommendationService.getUserRecommendations(currentUser.getUserId());
            model.addAttribute("recommendations", recommendations);
            
            // 创建游戏Map方便前端查询推荐的游戏信息
            if (!recommendations.isEmpty()) {
                Map<Integer, Games> gameMap = new HashMap<>();
                for (com.graduation.entity.Recommendations rec : recommendations) {
                    try {
                        Games recommendedGame = gameService.getGameDetail(rec.getGameId());
                        gameMap.put(rec.getGameId(), recommendedGame);
                    } catch (Exception e) {
                        // 游戏可能已被删除，忽略
                    }
                }
                model.addAttribute("recommendedGameMap", gameMap);
            }
        }
        
        return "game/list";
    }
    
    /**
     * 显示游戏详情页面
     */
    @GetMapping("/detail/{id}")
    public String gameDetail(@PathVariable Integer id, Model model, HttpSession session) {
        Games game = gameService.getGameDetail(id);
        model.addAttribute("game", game);
        
        // 获取游戏的已审核评论
        List<Comments> comments = commentService.getApprovedComments(id);
        model.addAttribute("comments", comments);
        
        // 获取当前用户信息（用于判断是否可以编辑）
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
            // 判断是否是发布者本人
            boolean isPublisher = game.getPublisherId().equals(currentUser.getUserId());
            model.addAttribute("isPublisher", isPublisher);
            
            // 检查是否已收藏
            boolean isFavorited = favoriteService.isFavorited(currentUser.getUserId(), id);
            model.addAttribute("isFavorited", isFavorited);
            
            // 为每个评论添加点赞数和是否已点赞信息
            Map<Integer, Integer> likeCounts = new HashMap<>();
            Map<Integer, Boolean> userLiked = new HashMap<>();
            for (Comments comment : comments) {
                likeCounts.put(comment.getCommentId(), commentService.getLikeCount(comment.getCommentId()));
                userLiked.put(comment.getCommentId(), commentService.isLikedByUser(comment.getCommentId(), currentUser.getUserId()));
            }
            model.addAttribute("likeCounts", likeCounts);
            model.addAttribute("userLiked", userLiked);
        } else {
            // 未登录用户也显示点赞数，但不显示是否已点赞
            Map<Integer, Integer> likeCounts = new HashMap<>();
            for (Comments comment : comments) {
                likeCounts.put(comment.getCommentId(), commentService.getLikeCount(comment.getCommentId()));
            }
            model.addAttribute("likeCounts", likeCounts);
        }
        
        return "game/detail";
    }
    
    /**
     * 显示发布游戏页面
     */
    @GetMapping("/publish")
    public String showPublishPage(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 检查用户角色（必须是publisher或admin）
        if (!"publisher".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole())) {
            model.addAttribute("error", "您没有权限发布游戏，请联系管理员升级为发布者");
            return "error";
        }
        
        model.addAttribute("gameSubmitDTO", new GameSubmitDTO());
        return "game/publish";
    }
    
    /**
     * 处理发布游戏请求（支持图片上传）
     */
    @PostMapping("/publish")
    public String publishGame(@Valid @ModelAttribute GameSubmitDTO dto,
                             BindingResult bindingResult,
                             @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                             @RequestParam(value = "otherImages", required = false) MultipartFile[] otherImages,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 检查用户角色
        if (!"publisher".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole())) {
            model.addAttribute("error", "您没有权限发布游戏");
            return "error";
        }
        
        if (bindingResult.hasErrors()) {
            return "game/publish";
        }
        
        try {
            // 处理封面图片上传
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverImageUrl = fileUploadUtil.uploadFile(coverImage);
                dto.setCoverImageUrl(coverImageUrl);
            }
            
            // 处理其他图片上传
            if (otherImages != null && otherImages.length > 0) {
                String otherImageUrls = fileUploadUtil.uploadMultipleFiles(otherImages);
                dto.setOtherImageUrls(otherImageUrls);
            }
            
            Games game = gameService.publishGame(dto, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "游戏发布成功，等待管理员审核");
            return "redirect:/game/my-games";
        } catch (Exception e) {
            model.addAttribute("error", "图片上传失败：" + e.getMessage());
            return "game/publish";
        }
    }
    
    /**
     * 显示编辑游戏页面
     */
    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable Integer id, HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        Games game = gameService.getGameDetail(id);
        
        // 验证权限：只有发布者本人可以编辑
        if (!game.getPublisherId().equals(currentUser.getUserId())) {
            model.addAttribute("error", "您没有权限编辑此游戏");
            return "error";
        }
        
        // 将游戏数据转换为DTO
        GameSubmitDTO dto = new GameSubmitDTO();
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setCoverImageUrl(game.getCoverImageUrl());
        dto.setOtherImageUrls(game.getOtherImageUrls());
        
        model.addAttribute("game", game);
        model.addAttribute("gameSubmitDTO", dto);
        return "game/edit";
    }
    
    /**
     * 处理编辑游戏请求（支持图片上传）
     */
    @PostMapping("/edit/{id}")
    public String updateGame(@PathVariable Integer id,
                           @Valid @ModelAttribute GameSubmitDTO dto,
                           BindingResult bindingResult,
                           @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                           @RequestParam(value = "otherImages", required = false) MultipartFile[] otherImages,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        if (bindingResult.hasErrors()) {
            Games game = gameService.getGameDetail(id);
            model.addAttribute("game", game);
            return "game/edit";
        }
        
        try {
            // 获取原游戏信息
            Games existingGame = gameService.getGameDetail(id);
            
            // 处理封面图片上传
            if (coverImage != null && !coverImage.isEmpty()) {
                // 删除旧的封面图片
                if (existingGame.getCoverImageUrl() != null && !existingGame.getCoverImageUrl().isEmpty()) {
                    fileUploadUtil.deleteFile(existingGame.getCoverImageUrl());
                }
                String coverImageUrl = fileUploadUtil.uploadFile(coverImage);
                dto.setCoverImageUrl(coverImageUrl);
            } else {
                // 保留原封面图片
                dto.setCoverImageUrl(existingGame.getCoverImageUrl());
            }
            
            // 处理其他图片上传
            if (otherImages != null && otherImages.length > 0) {
                // 删除旧的其他图片
                if (existingGame.getOtherImageUrls() != null && !existingGame.getOtherImageUrls().isEmpty()) {
                    fileUploadUtil.deleteMultipleFiles(existingGame.getOtherImageUrls());
                }
                String otherImageUrls = fileUploadUtil.uploadMultipleFiles(otherImages);
                dto.setOtherImageUrls(otherImageUrls);
            } else {
                // 保留原其他图片
                dto.setOtherImageUrls(existingGame.getOtherImageUrls());
            }
            
            gameService.updateGame(id, dto, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "游戏信息更新成功");
            return "redirect:/game/my-games";
        } catch (Exception e) {
            Games game = gameService.getGameDetail(id);
            model.addAttribute("game", game);
            model.addAttribute("error", "更新失败：" + e.getMessage());
            return "game/edit";
        }
    }
    
    /**
     * 显示"我的游戏"页面
     */
    @GetMapping("/my-games")
    public String myGames(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<Games> games = gameService.getGamesByPublisherId(currentUser.getUserId());
        model.addAttribute("games", games);
        model.addAttribute("currentUser", currentUser);
        
        // 获取每个游戏的撤回申请状态
        Map<Integer, com.graduation.entity.WithdrawalRequests> withdrawalRequests = new HashMap<>();
        for (Games game : games) {
            List<com.graduation.entity.WithdrawalRequests> requests = withdrawalService.getWithdrawalRequestsByGameId(game.getGameId());
            // 只显示最新的待审核撤回申请
            for (com.graduation.entity.WithdrawalRequests request : requests) {
                if ("pending".equals(request.getStatus())) {
                    withdrawalRequests.put(game.getGameId(), request);
                    break;
                }
            }
        }
        model.addAttribute("withdrawalRequests", withdrawalRequests);
        
        return "game/my-games";
    }
    
    /**
     * 处理撤回申请提交
     */
    @PostMapping("/withdrawal/submit")
    public String submitWithdrawalRequest(@RequestParam Integer gameId,
                                         @RequestParam String reason,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        try {
            withdrawalService.submitWithdrawalRequest(gameId, currentUser.getUserId(), reason);
            redirectAttributes.addFlashAttribute("success", "撤回申请已提交，等待管理员审核");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/game/my-games";
    }
}
