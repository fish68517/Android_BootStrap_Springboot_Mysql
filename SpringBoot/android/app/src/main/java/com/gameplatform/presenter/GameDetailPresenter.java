package com.gameplatform.presenter;

import com.gameplatform.base.BasePresenter;
import com.gameplatform.contract.GameDetailContract;
import com.gameplatform.dto.request.CommentRequest;
import com.gameplatform.dto.request.FavoriteRequest;
import com.gameplatform.dto.response.ApiResponse;
import com.gameplatform.model.Comment;
import com.gameplatform.model.Game;
import com.gameplatform.repository.GameRepository;
import com.gameplatform.util.FavoriteManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameDetailPresenter extends BasePresenter implements GameDetailContract.Presenter {
    
    private GameDetailContract.View view;
    private GameRepository gameRepository;
    
    public GameDetailPresenter(GameDetailContract.View view) {
        this.view = view;
        this.gameRepository = new GameRepository();
    }
    
    @Override
    public void loadGameDetail(String gameId) {
        view.showLoading();
        
        Call<ApiResponse<Game>> call = gameRepository.getGameDetail(gameId);
        call.enqueue(new Callback<ApiResponse<Game>>() {
            @Override
            public void onResponse(Call<ApiResponse<Game>> call, Response<ApiResponse<Game>> response) {
                view.hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Game> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        view.showGameDetail(apiResponse.getData());
                    } else {
                        view.showError(apiResponse.getMessage());
                    }
                } else {
                    view.showError("加载游戏详情失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Game>> call, Throwable t) {
                view.hideLoading();
                view.showError("网络连接失败");
            }
        });
    }
    
    @Override
    public void loadComments(String gameId) {
        Call<ApiResponse<List<Comment>>> call = gameRepository.getComments(gameId);
        call.enqueue(new Callback<ApiResponse<List<Comment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Comment>>> call, Response<ApiResponse<List<Comment>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Comment>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Comment> comments = apiResponse.getData();
                        if (comments == null) {
                            comments = new ArrayList<>();
                        }
                        view.showComments(comments);
                    } else {
                        view.showError(apiResponse.getMessage());
                    }
                } else {
                    view.showError("加载评论失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Comment>>> call, Throwable t) {
                // Show empty comments list on network failure
                view.showComments(new ArrayList<>());
            }
        });
    }
    
    @Override
    public void submitComment(String gameId, String content) {
        CommentRequest request = new CommentRequest();
        request.setGameId(gameId);
        request.setContent(content);
        
        Call<ApiResponse<Comment>> call = gameRepository.addComment(request);
        call.enqueue(new Callback<ApiResponse<Comment>>() {
            @Override
            public void onResponse(Call<ApiResponse<Comment>> call, Response<ApiResponse<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Comment> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        view.showCommentAdded(apiResponse.getData());
                    } else {
                        view.showError(apiResponse.getMessage());
                    }
                } else {
                    view.showError("发表评论失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Comment>> call, Throwable t) {
                view.showError("网络连接失败");
            }
        });
    }
    
    @Override
    public void likeComment(String commentId, int position) {
        // In a real implementation, you'd need to track the current like state
        // For now, we'll simulate toggling the like state
        toggleCommentLike(commentId, position, false); // Assume not liked initially
    }
    
    public void likeCommentWithState(String commentId, int position, boolean currentlyLiked) {
        toggleCommentLike(commentId, position, currentlyLiked);
    }
    
    private void toggleCommentLike(String commentId, int position, boolean currentlyLiked) {
        Call<ApiResponse<Void>> call;
        
        if (currentlyLiked) {
            // Currently liked, so unlike
            call = gameRepository.unlikeComment(commentId);
        } else {
            // Not currently liked, so like
            call = gameRepository.likeComment(commentId);
        }
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Toggle the like state
                        boolean newLikedState = !currentlyLiked;
                        int newLikeCount = currentlyLiked ? -1 : 1; // Simulate count change
                        view.showCommentLiked(position, newLikedState, newLikeCount);
                    } else {
                        view.showError(apiResponse.getMessage());
                    }
                } else {
                    view.showError("点赞操作失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                view.showError("网络连接失败");
            }
        });
    }
    
    @Override
    public void toggleFavorite(String gameId) {
        // Get current favorite state from the view's current game
        // In a real implementation, you'd track this state properly
        toggleFavoriteWithState(gameId, false); // Start with assumption it's not favorited
    }
    
    @Override
    public void toggleFavoriteWithCurrentState(String gameId, boolean currentlyFavorited) {
        toggleFavoriteWithState(gameId, currentlyFavorited);
    }
    
    private void toggleFavoriteWithState(String gameId, boolean currentlyFavorited) {
        Call<ApiResponse<Void>> call;
        
        if (currentlyFavorited) {
            // Currently favorited, so remove from favorites
            call = gameRepository.removeFavorite(gameId);
        } else {
            // Not currently favorited, so add to favorites
            FavoriteRequest request = new FavoriteRequest();
            request.setGameId(gameId);
            call = gameRepository.addFavorite(request);
        }
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Toggle the favorite state
                        boolean newFavoriteState = !currentlyFavorited;
                        
                        // Update FavoriteManager
                        FavoriteManager.getInstance().updateFavoriteStatus(gameId, newFavoriteState);
                        
                        view.showFavoriteToggled(newFavoriteState);
                    } else {
                        view.showError(apiResponse.getMessage());
                    }
                } else {
                    view.showError("收藏操作失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                view.showError("网络连接失败");
            }
        });
    }
}