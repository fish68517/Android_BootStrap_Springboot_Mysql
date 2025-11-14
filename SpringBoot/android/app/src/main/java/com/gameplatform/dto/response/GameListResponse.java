package com.gameplatform.dto.response;

import com.gameplatform.model.Game;
import java.util.List;

/**
 * Response DTO for game list
 * Requirements: 3.1, 3.2, 3.3
 */
public class GameListResponse {
    private List<Game> games;
    private int currentPage;
    private int totalPages;
    private int totalCount;
    private boolean hasMore;

    public GameListResponse() {}

    public GameListResponse(List<Game> games, int currentPage, int totalPages, int totalCount, boolean hasMore) {
        this.games = games;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.hasMore = hasMore;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public String toString() {
        return "GameListResponse{" +
                "games=" + games +
                ", currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", totalCount=" + totalCount +
                ", hasMore=" + hasMore +
                '}';
    }
}