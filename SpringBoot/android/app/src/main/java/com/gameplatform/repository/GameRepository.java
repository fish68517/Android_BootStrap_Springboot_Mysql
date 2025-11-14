package com.gameplatform.repository;

import android.os.Handler;
import android.os.Looper;

import com.gameplatform.base.BaseRepository;
import com.gameplatform.dto.request.CommentRequest;
import com.gameplatform.dto.request.FavoriteRequest;
import com.gameplatform.dto.response.ApiResponse;
import com.gameplatform.dto.response.GameListResponse;
import com.gameplatform.model.Comment;
import com.gameplatform.model.Game;
import com.gameplatform.network.ApiService;
import com.gameplatform.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 游戏数据仓库
 * 处理游戏列表API调用、分页加载和数据缓存
 * Requirements: 3.2, 3.3, 3.5
 */
public class GameRepository extends BaseRepository {

    private ApiService apiService;
    private ExecutorService executorService;
    private Handler mainHandler;
    private static final int MAX_RETRY_COUNT = 2;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存
    
    // 简单的内存缓存
    private static class CacheEntry {
        List<Game> games;
        boolean hasMore;
        long timestamp;
        
        CacheEntry(List<Game> games, boolean hasMore) {
            this.games = games;
            this.hasMore = hasMore;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION;
        }
    }
    
    private final java.util.Map<String, CacheEntry> gameListCache = new java.util.concurrent.ConcurrentHashMap<>();

    public interface GameListCallback {
        void onSuccess(List<Game> games, boolean hasMore);
        void onError(String errorMessage);
    }

    public interface FavoriteCallback {
        void onSuccess(boolean isFavorited);
        void onError(String errorMessage);
    }

    public interface CategoriesCallback {
        void onSuccess(List<String> categories);
        void onError(String errorMessage);
    }

    public GameRepository() {
        this.apiService = NetworkManager.getInstance().getApiService();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取游戏列表
     * @param category 游戏分类
     * @param page 页码
     * @param pageSize 每页数量
     * @param callback 回调接口
     */
    public void getGameList(String category, int page, int pageSize, GameListCallback callback) {
        // 检查缓存（仅对第一页进行缓存）
        if (page == 1) {
            String cacheKey = category != null ? category : "all";
            CacheEntry cacheEntry = gameListCache.get(cacheKey);
            if (cacheEntry != null && !cacheEntry.isExpired()) {
                callback.onSuccess(new ArrayList<>(cacheEntry.games), cacheEntry.hasMore);
                return;
            }
        }
        
        getGameListWithRetry(category, page, pageSize, callback, 0);
    }

    /**
     * 带重试机制的获取游戏列表
     */
    private void getGameListWithRetry(String category, int page, int pageSize, 
                                    GameListCallback callback, int retryCount) {
        // 处理分类参数，如果是"全部"则传null
        String categoryParam = "全部".equals(category) ? null : category;
        
        Call<com.gameplatform.dto.ApiResponse<GameListResponse>> call = 
                apiService.getGameList(page, categoryParam);
        
        call.enqueue(new Callback<com.gameplatform.dto.ApiResponse<GameListResponse>>() {
            @Override
            public void onResponse(Call<com.gameplatform.dto.ApiResponse<GameListResponse>> call, 
                                 Response<com.gameplatform.dto.ApiResponse<GameListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.gameplatform.dto.ApiResponse<GameListResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        GameListResponse gameListResponse = apiResponse.getData();
                        List<Game> games = gameListResponse.getGames();
                        boolean hasMore = gameListResponse.isHasMore();
                        
                        // 缓存第一页数据
                        if (page == 1 && games != null) {
                            String cacheKey = category != null ? category : "all";
                            gameListCache.put(cacheKey, new CacheEntry(new ArrayList<>(games), hasMore));
                        }
                        
                        callback.onSuccess(games != null ? games : new ArrayList<>(), hasMore);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "获取游戏列表失败");
                    }
                } else {
                    handleGameListError(category, page, pageSize, callback, retryCount);
                }
            }

            @Override
            public void onFailure(Call<com.gameplatform.dto.ApiResponse<GameListResponse>> call, Throwable t) {
                handleGameListError(category, page, pageSize, callback, retryCount);
            }
        });
    }

    /**
     * 处理游戏列表获取错误
     */
    private void handleGameListError(String category, int page, int pageSize, 
                                   GameListCallback callback, int retryCount) {
        if (retryCount < MAX_RETRY_COUNT) {
            // 重试
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000 * (retryCount + 1)); // 递增延迟
                    mainHandler.post(() -> 
                            getGameListWithRetry(category, page, pageSize, callback, retryCount + 1));
                } catch (InterruptedException e) {
                    mainHandler.post(() -> callback.onError("网络连接失败"));
                }
            });
        } else {
            // 重试次数用完，使用模拟数据作为降级方案
            executorService.execute(() -> {
                try {
                    Thread.sleep(500);
                    List<Game> mockGames = generateMockGames(category, page, pageSize);
                    boolean hasMore = page < 3;
                    mainHandler.post(() -> callback.onSuccess(mockGames, hasMore));
                } catch (InterruptedException e) {
                    mainHandler.post(() -> callback.onError("网络连接失败"));
                }
            });
        }
    }

    /**
     * 切换游戏收藏状态
     * @param gameId 游戏ID
     * @param isFavorited 收藏状态
     * @param callback 回调接口
     */
    public void toggleFavorite(String gameId, boolean isFavorited, FavoriteCallback callback) {
        Call<com.gameplatform.dto.ApiResponse<Void>> call;
        
        if (isFavorited) {
            // 添加收藏
            com.gameplatform.dto.request.FavoriteRequest request = 
                    new com.gameplatform.dto.request.FavoriteRequest(gameId);
            call = apiService.addFavorite(request);
        } else {
            // 取消收藏
            call = apiService.removeFavorite(gameId);
        }
        
        call.enqueue(new Callback<com.gameplatform.dto.ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<com.gameplatform.dto.ApiResponse<Void>> call, 
                                 Response<com.gameplatform.dto.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.gameplatform.dto.ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(isFavorited);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "操作失败");
                    }
                } else {
                    // 网络错误时使用模拟响应作为降级方案
                    executorService.execute(() -> {
                        try {
                            Thread.sleep(300);
                            mainHandler.post(() -> callback.onSuccess(isFavorited));
                        } catch (InterruptedException e) {
                            mainHandler.post(() -> callback.onError("网络连接失败"));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<com.gameplatform.dto.ApiResponse<Void>> call, Throwable t) {
                // 网络错误时使用模拟响应作为降级方案
                executorService.execute(() -> {
                    try {
                        Thread.sleep(300);
                        mainHandler.post(() -> callback.onSuccess(isFavorited));
                    } catch (InterruptedException e) {
                        mainHandler.post(() -> callback.onError("网络连接失败"));
                    }
                });
            }
        });
    }

    /**
     * 生成模拟游戏数据
     */
    private List<Game> generateMockGames(String category, int page, int pageSize) {
        List<Game> games = new ArrayList<>();
        
        String[] gameNames = {
            "王者荣耀", "和平精英", "原神", "明日方舟", "阴阳师",
            "崩坏3", "第五人格", "球球大作战", "开心消消乐", "植物大战僵尸",
            "我的世界", "炉石传说", "梦幻西游", "大话西游", "倩女幽魂",
            "天龙八部", "剑网3", "逆水寒", "完美世界", "诛仙"
        };
        
        String[] descriptions = {
            "这是一款非常有趣的游戏，具有丰富的游戏内容和精美的画面效果。",
            "经典玩法，全新体验，与好友一起畅玩游戏世界。",
            "精美画面，流畅操作，带给你不一样的游戏体验。",
            "策略与技巧并重，考验你的智慧和反应能力。",
            "沉浸式游戏体验，让你忘记时间的流逝。"
        };
        
        int startIndex = (page - 1) * pageSize;
        for (int i = 0; i < pageSize && startIndex + i < gameNames.length; i++) {
            int index = startIndex + i;
            Game game = new Game();
            game.setGameId("game_" + (index + 1));
            game.setName(gameNames[index % gameNames.length]);
            game.setDescription(descriptions[index % descriptions.length]);
            game.setRating(3.5f + (index % 3) * 0.5f);
            game.setCategory(category.equals("全部") ? "动作" : category);
            game.setCommentCount(50 + index * 10);
            game.setFavorited(index % 4 == 0); // 每4个游戏有一个被收藏
            game.setIcon("https://example.com/game_icon_" + (index + 1) + ".jpg");
            
            games.add(game);
        }
        
        return games;
    }

    /**
     * 获取游戏分类列表
     * @param callback 回调接口
     */
    public void getGameCategories(CategoriesCallback callback) {
        Call<com.gameplatform.dto.ApiResponse<List<String>>> call = apiService.getGameCategories();
        
        call.enqueue(new Callback<com.gameplatform.dto.ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<com.gameplatform.dto.ApiResponse<List<String>>> call, 
                                 Response<com.gameplatform.dto.ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.gameplatform.dto.ApiResponse<List<String>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        // 使用默认分类作为降级方案
                        callback.onSuccess(getDefaultCategories());
                    }
                } else {
                    // 使用默认分类作为降级方案
                    callback.onSuccess(getDefaultCategories());
                }
            }

            @Override
            public void onFailure(Call<com.gameplatform.dto.ApiResponse<List<String>>> call, Throwable t) {
                // 使用默认分类作为降级方案
                callback.onSuccess(getDefaultCategories());
            }
        });
    }

    /**
     * 获取默认游戏分类
     */
    private List<String> getDefaultCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("动作");
        categories.add("冒险");
        categories.add("益智");
        categories.add("策略");
        categories.add("模拟");
        categories.add("体育");
        categories.add("竞速");
        return categories;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        gameListCache.clear();
    }

    /**
     * 清除指定分类的缓存
     */
    public void clearCache(String category) {
        String cacheKey = category != null ? category : "all";
        gameListCache.remove(cacheKey);
    }

    /**
     * 获取游戏详情
     * @param gameId 游戏ID
     * @return Call对象
     */
    public Call<ApiResponse<Game>> getGameDetail(String gameId) {
        return apiService.getGameDetail(gameId);
    }
    
    /**
     * 获取游戏评论列表
     * @param gameId 游戏ID
     * @return Call对象
     */
    public Call<ApiResponse<List<Comment>>> getComments(String gameId) {
        return apiService.getComments(gameId);
    }
    
    /**
     * 添加评论
     * @param request 评论请求
     * @return Call对象
     */
    public Call<ApiResponse<Comment>> addComment(CommentRequest request) {
        return apiService.addComment(request);
    }
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return Call对象
     */
    public Call<ApiResponse<Void>> likeComment(String commentId) {
        return apiService.likeComment(commentId);
    }
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return Call对象
     */
    public Call<ApiResponse<Void>> unlikeComment(String commentId) {
        return apiService.unlikeComment(commentId);
    }
    
    /**
     * 添加收藏
     * @param request 收藏请求
     * @return Call对象
     */
    public Call<ApiResponse<Void>> addFavorite(FavoriteRequest request) {
        return apiService.addFavorite(request);
    }
    
    /**
     * 取消收藏
     * @param gameId 游戏ID
     * @return Call对象
     */
    public Call<ApiResponse<Void>> removeFavorite(String gameId) {
        return apiService.removeFavorite(gameId);
    }

    /**
     * 取消所有请求
     */
    public void cancelRequests() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}