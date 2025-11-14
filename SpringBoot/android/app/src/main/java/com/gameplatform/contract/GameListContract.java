package com.gameplatform.contract;

import com.gameplatform.base.BaseView;
import com.gameplatform.model.Game;
import java.util.List;

/**
 * Contract interface for Game List module
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5
 */
public interface GameListContract {
    
    interface View extends BaseView {
        /**
         * Show game list
         * @param games List of games
         * @param isRefresh Whether this is a refresh operation
         */
        void showGameList(List<Game> games, boolean isRefresh);
        
        /**
         * Show load more games
         * @param games Additional games to load
         */
        void showLoadMoreGames(List<Game> games);
        
        /**
         * Show no more games available
         */
        void showNoMoreGames();
        
        /**
         * Show empty game list
         */
        void showEmptyGameList();
        
        /**
         * Navigate to game detail
         * @param game Selected game
         */
        void navigateToGameDetail(Game game);
        
        /**
         * Show game categories
         * @param categories List of game categories
         */
        void showGameCategories(List<String> categories);
        
        /**
         * Update game favorite status
         * @param gameId Game ID
         * @param isFavorited New favorite status
         */
        void updateGameFavoriteStatus(String gameId, boolean isFavorited);
    }
    
    interface Presenter {
        /**
         * Load game list
         * @param category Game category filter
         * @param isRefresh Whether this is a refresh operation
         */
        void loadGameList(String category, boolean isRefresh);
        
        /**
         * Load more games
         * @param category Game category filter
         */
        void loadMoreGames(String category);
        
        /**
         * Refresh game list
         * @param category Game category filter
         */
        void refreshGameList(String category);
        
        /**
         * Handle game item click
         * @param game Clicked game
         */
        void onGameItemClick(Game game);
        
        /**
         * Handle favorite button click
         * @param game Game to favorite/unfavorite
         */
        void onFavoriteClick(Game game);
        
        /**
         * Load game categories
         */
        void loadGameCategories();
        
        /**
         * Handle category selection
         * @param category Selected category
         */
        void onCategorySelected(String category);
    }
}