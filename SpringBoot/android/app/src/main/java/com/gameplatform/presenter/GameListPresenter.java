package com.gameplatform.presenter;

import com.gameplatform.base.BasePresenter;
import com.gameplatform.contract.GameListContract;
import com.gameplatform.model.Game;
import com.gameplatform.repository.GameRepository;

import java.util.Arrays;
import java.util.List;

/**
 * 游戏列表Presenter
 * 处理游戏列表业务逻辑、分页加载和收藏功能
 * Requirements: 3.2, 3.3, 3.5
 */
public class GameListPresenter extends BasePresenter implements GameListContract.Presenter {

    private GameListContract.View view;
    private GameRepository gameRepository;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;

    public GameListPresenter(GameListContract.View view) {
        this.view = view;
        this.gameRepository = new GameRepository();
    }

    @Override
    public void loadGameList(String category, boolean isRefresh) {
        if (isRefresh) {
            currentPage = 1;
        }
        
        if (view != null) {
            view.showLoading();
        }

        // 模拟API调用 - 后续任务中会实现真实的API调用
        gameRepository.getGameList(category, currentPage, PAGE_SIZE, new GameRepository.GameListCallback() {
            @Override
            public void onSuccess(List<Game> games, boolean hasMore) {
                if (view != null) {
                    view.hideLoading();
                    if (games.isEmpty() && currentPage == 1) {
                        view.showEmptyGameList();
                    } else {
                        view.showGameList(games, isRefresh);
                        if (!hasMore) {
                            view.showNoMoreGames();
                        }
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (view != null) {
                    view.hideLoading();
                    view.showError(errorMessage);
                }
            }
        });
    }

    @Override
    public void loadMoreGames(String category) {
        currentPage++;
        
        gameRepository.getGameList(category, currentPage, PAGE_SIZE, new GameRepository.GameListCallback() {
            @Override
            public void onSuccess(List<Game> games, boolean hasMore) {
                if (view != null) {
                    if (games.isEmpty()) {
                        view.showNoMoreGames();
                    } else {
                        view.showLoadMoreGames(games);
                        if (!hasMore) {
                            view.showNoMoreGames();
                        }
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (view != null) {
                    view.showError(errorMessage);
                }
                currentPage--; // 回退页码
            }
        });
    }

    @Override
    public void refreshGameList(String category) {
        // 清除缓存以确保获取最新数据
        gameRepository.clearCache(category);
        loadGameList(category, true);
    }

    @Override
    public void onGameItemClick(Game game) {
        if (view != null) {
            view.navigateToGameDetail(game);
        }
    }

    @Override
    public void onFavoriteClick(Game game) {
        // 切换收藏状态
        boolean newFavoriteStatus = !game.isFavorited();
        
        // 模拟API调用 - 后续任务中会实现真实的API调用
        gameRepository.toggleFavorite(game.getGameId(), newFavoriteStatus, new GameRepository.FavoriteCallback() {
            @Override
            public void onSuccess(boolean isFavorited) {
                if (view != null) {
                    game.setFavorited(isFavorited);
                    view.updateGameFavoriteStatus(game.getGameId(), isFavorited);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (view != null) {
                    view.showError(errorMessage);
                }
            }
        });
    }

    @Override
    public void loadGameCategories() {
        gameRepository.getGameCategories(new GameRepository.CategoriesCallback() {
            @Override
            public void onSuccess(List<String> categories) {
                if (view != null) {
                    view.showGameCategories(categories);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // 使用默认分类作为降级方案
                List<String> defaultCategories = Arrays.asList(
                        "动作", "冒险", "益智", "策略", "模拟", "体育", "竞速"
                );
                if (view != null) {
                    view.showGameCategories(defaultCategories);
                }
            }
        });
    }

    @Override
    public void onCategorySelected(String category) {
        currentPage = 1;
        loadGameList(category, true);
    }

    @Override
    public void onDestroy() {
        view = null;
        if (gameRepository != null) {
            gameRepository.cancelRequests();
        }
    }
}