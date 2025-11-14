package com.gameplatform.contract;

import com.gameplatform.base.BaseView;
import com.gameplatform.model.Comment;
import com.gameplatform.model.Game;
import java.util.List;

/**
 * Contract interface for Game Detail module
 * Requirements: 3.4, 4.1, 4.2, 5.1, 5.2, 5.3, 5.4, 5.5
 */
public interface GameDetailContract {
    
    interface View extends BaseView {
        /**
         * Show game detail
         * @param game Game detail information
         */
        void showGameDetail(Game game);
        
        /**
         * Show game comments
         * @param comments List of comments
         */
        void showComments(List<Comment> comments);
        
        /**
         * Show comment added successfully
         * @param comment New comment
         */
        void showCommentAdded(Comment comment);
        
        /**
         * Show comment like updated
         * @param position Comment position
         * @param isLiked New like status
         * @param likeCount New like count
         */
        void showCommentLiked(int position, boolean isLiked, int likeCount);
        
        /**
         * Show favorite status updated
         * @param isFavorited New favorite status
         */
        void showFavoriteToggled(boolean isFavorited);
        
        /**
         * Clear comment input
         */
        void clearCommentInput();
        
        /**
         * Show comment input error
         * @param message Error message
         */
        void showCommentInputError(String message);
    }
    
    interface Presenter {
        /**
         * Load game detail
         * @param gameId Game ID
         */
        void loadGameDetail(String gameId);
        
        /**
         * Load game comments
         * @param gameId Game ID
         */
        void loadGameComments(String gameId);
        
        /**
         * Submit comment
         * @param gameId Game ID
         * @param content Comment content
         */
        void submitComment(String gameId, String content);
        
        /**
         * Like comment
         * @param commentId Comment ID
         * @param position Comment position
         */
        void likeComment(String commentId, int position);
        
        /**
         * Toggle favorite
         * @param gameId Game ID
         */
        void toggleFavorite(String gameId);
        
        /**
         * Toggle game favorite
         * @param gameId Game ID
         */
        void toggleGameFavorite(String gameId);
        
        /**
         * Toggle game favorite with current state
         * @param gameId Game ID
         * @param currentlyFavorited Current favorite state
         */
        void toggleFavoriteWithCurrentState(String gameId, boolean currentlyFavorited);
        
        /**
         * Validate comment content
         * @param content Comment content
         * @return true if valid, false otherwise
         */
        boolean validateCommentContent(String content);
    }
}