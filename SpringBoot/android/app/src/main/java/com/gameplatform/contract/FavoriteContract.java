package com.gameplatform.contract;

import com.gameplatform.base.BaseView;
import com.gameplatform.model.Game;
import java.util.List;

/**
 * Contract interface for Favorite module
 * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5
 */
public interface FavoriteContract {
    
    interface View extends BaseView {
        /**
         * Show favorite games list
         * @param games List of favorite games
         */
        void showFavoriteGames(List<Game> games);
        
        /**
         * Show empty favorite list
         */
        void showEmptyFavoriteList();
        
        /**
         * Show game removed from favorites
         * @param gameId Removed game ID
         */
        void onGameRemovedFromFavorites(String gameId);
        
        /**
         * Navigate to game detail
         * @param game Selected game
         */
        void navigateToGameDetail(Game game);
        
        /**
         * Navigate to game list
         */
        void navigateToGameList();
        
        /**
         * Show remove favorite confirmation dialog
         * @param game Game to remove
         */
        void showRemoveFavoriteConfirmation(Game game);
    }
    
    interface Presenter {
        /**
         * Load favorite games
         */
        void loadFavoriteGames();
        
        /**
         * Refresh favorite games
         */
        void refreshFavoriteGames();
        
        /**
         * Handle game item click
         * @param game Clicked game
         */
        void onGameItemClick(Game game);
        
        /**
         * Handle remove favorite click
         * @param game Game to remove from favorites
         */
        void onRemoveFavoriteClick(Game game);
        
        /**
         * Confirm remove favorite
         * @param game Game to remove from favorites
         */
        void confirmRemoveFavorite(Game game);
        
        /**
         * Handle discover games click
         */
        void onDiscoverGamesClick();
    }
}