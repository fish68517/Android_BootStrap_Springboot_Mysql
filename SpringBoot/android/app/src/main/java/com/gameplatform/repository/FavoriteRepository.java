package com.gameplatform.repository;

import com.gameplatform.base.BaseRepository;
import com.gameplatform.dto.ApiResponse;
import com.gameplatform.dto.request.FavoriteRequest;
import com.gameplatform.model.Game;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 收藏功能数据仓库
 * 处理收藏相关的API调用和数据管理
 * Requirements: 4.1, 4.2, 4.5
 */
public class FavoriteRepository extends BaseRepository {

    private Call<ApiResponse<List<Game>>> getFavoriteGamesCall;
    private Call<ApiResponse<Void>> addFavoriteCall;
    private Call<ApiResponse<Void>> removeFavoriteCall;

    /**
     * 获取收藏游戏列表
     * @param callback 回调接口
     */
    public void getFavoriteGames(Callback<List<Game>> callback) {
        // 取消之前的请求
        if (getFavoriteGamesCall != null && !getFavoriteGamesCall.isCanceled()) {
            getFavoriteGamesCall.cancel();
        }

        getFavoriteGamesCall = apiService.getFavoriteList();
        getFavoriteGamesCall.enqueue(new Callback<ApiResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Game>>> call, Response<ApiResponse<List<Game>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Game>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Game> favoriteGames = apiResponse.getData();
                        // 确保所有游戏都标记为已收藏
                        if (favoriteGames != null) {
                            for (Game game : favoriteGames) {
                                game.setFavorited(true);
                            }
                        }
                        callback.onResponse(call, Response.success(favoriteGames));
                    } else {
                        callback.onResponse(call, Response.success(null));
                    }
                } else {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Game>>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * 添加游戏到收藏
     * @param gameId 游戏ID
     * @param callback 回调接口
     */
    public void addFavorite(String gameId, Callback<Void> callback) {
        // 取消之前的请求
        if (addFavoriteCall != null && !addFavoriteCall.isCanceled()) {
            addFavoriteCall.cancel();
        }

        FavoriteRequest request = new FavoriteRequest(gameId);
        addFavoriteCall = apiService.addFavorite(request);
        addFavoriteCall.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onResponse(call, Response.success(null));
                    } else {
                        callback.onResponse(call, Response.success(null));
                    }
                } else {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * 从收藏中移除游戏
     * @param gameId 游戏ID
     * @param callback 回调接口
     */
    public void removeFavorite(String gameId, Callback<Void> callback) {
        // 取消之前的请求
        if (removeFavoriteCall != null && !removeFavoriteCall.isCanceled()) {
            removeFavoriteCall.cancel();
        }

        removeFavoriteCall = apiService.removeFavorite(gameId);
        removeFavoriteCall.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onResponse(call, Response.success(null));
                    } else {
                        callback.onResponse(call, Response.success(null));
                    }
                } else {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAllRequests() {
        if (getFavoriteGamesCall != null && !getFavoriteGamesCall.isCanceled()) {
            getFavoriteGamesCall.cancel();
        }
        if (addFavoriteCall != null && !addFavoriteCall.isCanceled()) {
            addFavoriteCall.cancel();
        }
        if (removeFavoriteCall != null && !removeFavoriteCall.isCanceled()) {
            removeFavoriteCall.cancel();
        }
    }
}