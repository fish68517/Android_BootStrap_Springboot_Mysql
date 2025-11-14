package com.archive.app;
import com.archive.app.model.GameCategory;
import com.archive.app.model.dto.CommentPostDTO;
import com.archive.app.model.dto.FavoriteRequestDTO;
import com.archive.app.model.dto.PreferencesSaveDTO;
import com.archive.app.model.dto.UserLoginDTO;
import com.archive.app.model.dto.UserRegisterDTO;
import com.archive.app.model.dto.WithdrawalSubmitDTO;
import com.archive.app.model.entity.Game;
import com.archive.app.model.entity.Preference;
import com.archive.app.model.entity.Recommendation;
import com.archive.app.model.entity.User;
import com.archive.app.model.response.ApiResponse;
import com.archive.app.model.response.LikeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 定义所有与后端Spring Boot的API接口 (完整版)
 * 路径基于您提供的 Controller 文件
 */
public interface ApiService {

    //=========================== 1. 用户 (User) ===========================
    // 对应: UserController.java

    /**
     * 用户登录
     * @param loginDTO 包含用户名和密码
     */
    @POST("/api/user/login")
    Call<User> login(@Body UserLoginDTO loginDTO);

    /**
     * 用户注册
     * @param registerDTO 包含注册信息
     */
    @POST("/api/user/register")
    Call<Boolean> register(@Body UserRegisterDTO registerDTO);

    /**
     * 用户登出 (假设的 JSON 接口)
     */
    @POST("/api/user/logout")
    Call<ApiResponse> logout();

    /**
     * 获取用户个人资料 (假设的 JSON 接口)
     * (UserController.java 中的 /api/user/profile 是网页)
     */
    @GET("/api/user/profile") // 假设这是JSON API路径
    Call<User> getProfile();

    /**
     * 更新用户个人资料 (假设的 JSON 接口)
     * (UserController.java 中的 /api/user/profile/update 是网页)
     */
    @POST("/api/user/profile/update") // 假设这是JSON API路径
    Call<User> updateProfile(@Body User user);

    /**
     * 获取所有可选偏好 (假设的 JSON 接口)
     * (UserController.java 中的 /api/user/preferences 是网页)
     */
    @GET("/api/user/preferences") // 假设这是JSON API路径
    Call<List<Preference>> getAllPreferences();

    /**
     * 保存用户偏好 (假设的 JSON 接口)
     * (UserController.java 中的 /api/user/preferences/save 是网页)
     */
    @POST("/api/user/preferences/save") // 假设这是JSON API路径
    Call<ApiResponse> savePreferences(@Body PreferencesSaveDTO preferencesSaveDTO);


    //=========================== 2. 游戏 (Game) ===========================
    // 对应: GameController.java

    /**
     * 获取所有游戏列表 (假设的 JSON 接口)
     * (GameController.java 中的 /game/list 是网页)
     */
    @GET("/api/game/list") // 假设这是JSON API路径
    Call<List<Game>> getAllGames();

    /**
     * 搜索游戏 (假设的 JSON 接口)
     * (GameController.java 中的 /game/search 是网页)
     * @param query 搜索关键词
     */
    @GET("/api/game/search") // 假设这是JSON API路径
    Call<List<Game>> searchGames(@Query("query") String query);


    /**
     * 【新增】获取所有游戏分类
     */
    @GET("/api/game/categories") // 假设后端提供了此接口
    Call<List<GameCategory>> getGameCategories();

    /**
     * 【新增】按分类和搜索词获取游戏列表
     * @param category 分类名 (例如 "RPG", "全部")
     * @param query 搜索词 (可为空)
     */
    @GET("/api/game/list") // 对应后端 GameApiController
    Call<List<Game>> getGames(@Query("category") String category, @Query("query") String query);

    /**
     * 获取游戏详情 (假设的 JSON 接口)
     * (GameController.java 中的 /game/detail/{id} 是网页)
     * @param id 游戏ID
     */
    @GET("/api/game/detail/{id}") // 假设这是JSON API路径
    Call<Game> getGameDetail(@Path("id") int id);

    /**
     * 获取我发布的游戏 (假设的 JSON 接口)
     * (GameController.java 中的 /game/my-games 是网页)
     */
    @GET("/api/game/my-games") // 假设这是JSON API路径
    Call<List<Game>> getMyGames();

   /* *//**
     * 提交游戏撤回申请 (AJAX 接口)
     *//*
    @POST("/game/withdrawal/submit")
    Call<ApiResponse> submitWithdrawal(@Body WithdrawalSubmitDTO withdrawalSubmitDTO);*/

    /**
     * 获取首页推荐 (假设的 JSON 接口)
     * (GameController.java 中的 showIndex / 加载了 recommendations)
     */
    @GET("/api/recommendations") // 假设这是JSON API路径
    Call<List<Recommendation>> getRecommendations();


    //=========================== 3. 收藏 (Favorite) ===========================
    // 对应: FavoriteController.java

    /**
     * 添加收藏 (AJAX 接口)
     */
    @POST("/api/favorite/add")
    Call<ApiResponse> addFavorite(@Body FavoriteRequestDTO favoriteRequestDTO);

    /**
     *
     * 取消收藏 (AJAX 接口)
     */
    @POST("/api/favorite/remove")
    Call<ApiResponse> removeFavorite(@Body FavoriteRequestDTO favoriteRequestDTO);

    /**
     * 获取我的收藏列表 (假设的 JSON 接口)
     * (FavoriteController.java 中的 /api/favorite/list 是网页)
     */
    @GET("/api/favorite/list") // 假设这是JSON API路径
    Call<List<Game>> getMyFavorites();


    //=========================== 4. 评论 (Comment) ===========================
    // 对应: CommentController.java

    /**
     * 发表评论 (假设的 JSON 接口)
     * (CommentController.java 中的 /comment/post 是网页)
     */
    @POST("/api/comment/post") // 假设这是JSON API路径
    Call<ApiResponse> postComment(@Body CommentPostDTO commentPostDTO);

    /**
     * 删除评论 (假设的 JSON 接口)
     * (CommentController.java 中的 /comment/delete/{id} 是网页)
     */
    @POST("/api/comment/delete/{id}") // 假设这是JSON API路径
    Call<ApiResponse> deleteComment(@Path("id") int id);

    /**
     * 点赞评论 (AJAX 接口)
     */
    @POST("/api/comment/like/{id}")
    Call<LikeResponse> likeComment(@Path("id") int id);

    /**
     * 取消点赞评论 (AJAX 接口)
     */
    @POST("/api/comment/unlike/{id}")
    Call<LikeResponse> unlikeComment(@Path("id") int id);

}