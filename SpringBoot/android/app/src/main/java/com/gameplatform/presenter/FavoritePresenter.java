package com.gameplatform.presenter;

import com.gameplatform.base.BasePresenter;
import com.gameplatform.contract.FavoriteContract;
import com.gameplatform.model.Game;
import com.gameplatform.repository.FavoriteRepository;
import com.gameplatform.util.FavoriteManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 收藏功能Presenter
 * 处理收藏列表加载、收藏状态管理和用户交互
 * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5
 */
public class FavoritePresenter extends BasePresenter<FavoriteContract.View> 
        implements FavoriteContract.Presenter {

    private FavoriteRepository favoriteRepository;

    public FavoritePresenter(FavoriteContract.View view) {
        super(view);
        this.favoriteRepository = new FavoriteRepository();
    }

    @Override
    public void loadFavoriteGames() {
        if (view == null) return;

        view.showLoading(true);

        favoriteRepository.getFavoriteGames(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (view == null) return;

                view.showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Game> favoriteGames = response.body();
                    
                    // 更新FavoriteManager中的收藏状态
                    Set<String> favoriteGameIds = new HashSet<>();
                    for (Game game : favoriteGames) {
                        favoriteGameIds.add(game.getGameId());
                    }
                    FavoriteManager.getInstance().setFavoriteGameIds(favoriteGameIds);
                    
                    if (favoriteGames.isEmpty()) {
                        view.showEmptyFavoriteList();
                    } else {
                        view.showFavoriteGames(favoriteGames);
                    }
                } else {
                    view.showError("加载收藏列表失败");
                    view.showEmptyFavoriteList();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                if (view == null) return;

                view.showLoading(false);
                view.showError("网络连接失败，请检查网络设置");
                view.showEmptyFavoriteList();
            }
        });
    }

    @Override
    public void refreshFavoriteGames() {
        loadFavoriteGames();
    }

    @Override
    public void onGameItemClick(Game game) {
        if (view == null) return;
        view.navigateToGameDetail(game);
    }

    @Override
    public void onRemoveFavoriteClick(Game game) {
        if (view == null) return;
        view.showRemoveFavoriteConfirmation(game);
    }

    @Override
    public void confirmRemoveFavorite(Game game) {
        if (view == null) return;

        view.showLoading(true);

        favoriteRepository.removeFavorite(game.getGameId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (view == null) return;

                view.showLoading(false);

                if (response.isSuccessful()) {
                    // 更新FavoriteManager中的收藏状态
                    FavoriteManager.getInstance().updateFavoriteStatus(game.getGameId(), false);
                    
                    view.onGameRemovedFromFavorites(game.getGameId());
                    view.showMessage("已取消收藏");
                } else {
                    view.showError("取消收藏失败");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (view == null) return;

                view.showLoading(false);
                view.showError("网络连接失败，请稍后重试");
            }
        });
    }

    @Override
    public void onDiscoverGamesClick() {
        if (view == null) return;
        view.navigateToGameList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (favoriteRepository != null) {
            favoriteRepository.cancelAllRequests();
        }
    }
}