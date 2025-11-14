package com.gameplatform.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.gameplatform.R;
import com.gameplatform.base.BaseFragment;
import com.gameplatform.contract.GameListContract;
import com.gameplatform.model.Game;
import com.gameplatform.presenter.GameListPresenter;
import com.gameplatform.util.LoadingStateUtil;
import com.gameplatform.util.NetworkStatusUtil;
import com.gameplatform.util.SkeletonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 游戏列表Fragment
 * 实现游戏列表展示、分类筛选、下拉刷新和分页加载功能
 * Requirements: 3.1, 3.2, 3.5
 */
public class GameListFragment extends BaseFragment implements GameListContract.View {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewGames;
    private TabLayout tabLayoutCategories;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBarLoading;
    private FloatingActionButton fabSearch;

    private GameListAdapter gameListAdapter;
    private GameListContract.Presenter presenter;
    private LinearLayoutManager layoutManager;

    private String currentCategory = "全部";
    private boolean isLoadingMore = false;
    
    private LoadingStateUtil.StateViewHolder stateViewHolder;
    private NetworkStatusUtil networkStatusUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_list, container, false);
    }

    @Override
    protected void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerViewGames = view.findViewById(R.id.recycler_view_games);
        tabLayoutCategories = view.findViewById(R.id.tab_layout_categories);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBarLoading = view.findViewById(R.id.progress_bar_loading);
        fabSearch = view.findViewById(R.id.fab_search);

        setupStateViewHolder(view);
        setupNetworkMonitoring();
        setupRecyclerView();
        setupSwipeRefresh();
        setupTabLayout();
        setupFab();
    }
    
    private void setupStateViewHolder(View view) {
        ViewGroup container = view.findViewById(R.id.state_container);
        if (container == null) {
            container = (ViewGroup) view; // Use root view as container
        }
        
        stateViewHolder = LoadingStateUtil.createStateViewHolder(container);
        stateViewHolder.setContentView(swipeRefreshLayout);
        
        stateViewHolder.setOnRetryClickListener(() -> {
            if (presenter != null) {
                presenter.loadGameList(currentCategory, false);
            }
        });
        
        stateViewHolder.setOnEmptyActionClickListener(() -> {
            // Navigate to discover or refresh
            if (presenter != null) {
                presenter.loadGameList(currentCategory, false);
            }
        });
    }
    
    private void setupNetworkMonitoring() {
        networkStatusUtil = NetworkStatusUtil.getInstance(requireContext());
        
        networkStatusUtil.addNetworkStatusListener(new NetworkStatusUtil.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable(NetworkStatusUtil.NetworkType networkType) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (stateViewHolder.isNoNetwork()) {
                            // Auto-retry when network becomes available
                            if (presenter != null) {
                                presenter.loadGameList(currentCategory, false);
                            }
                        }
                    });
                }
            }
            
            @Override
            public void onNetworkLost() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (stateViewHolder.isLoading()) {
                            stateViewHolder.showNoNetwork();
                        }
                    });
                }
            }
            
            @Override
            public void onNetworkChanged(NetworkStatusUtil.NetworkType networkType) {
                // Handle network type changes if needed
            }
        });
    }

    @Override
    protected void initData() {
        presenter = new GameListPresenter(this);
        presenter.loadGameCategories();
        presenter.loadGameList(currentCategory, false);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewGames.setLayoutManager(layoutManager);
        
        gameListAdapter = new GameListAdapter(new ArrayList<>());
        gameListAdapter.setOnItemClickListener(game -> {
            if (presenter != null) {
                presenter.onGameItemClick(game);
            }
        });
        gameListAdapter.setOnFavoriteClickListener(game -> {
            if (presenter != null) {
                presenter.onFavoriteClick(game);
            }
        });
        gameListAdapter.setOnLoadMoreListener(() -> {
            if (!isLoadingMore && presenter != null) {
                isLoadingMore = true;
                presenter.loadMoreGames(currentCategory);
            }
        });
        
        recyclerViewGames.setAdapter(gameListAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_color,
                R.color.secondary_color
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (presenter != null) {
                presenter.refreshGameList(currentCategory);
            }
        });
    }

    private void setupTabLayout() {
        tabLayoutCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText() != null ? tab.getText().toString() : "全部";
                currentCategory = category;
                if (presenter != null) {
                    presenter.onCategorySelected(category);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFab() {
        fabSearch.setOnClickListener(v -> {
            // TODO: 实现搜索功能 (后续任务)
            showMessage("搜索功能即将上线");
        });
    }

    // GameListContract.View implementation

    @Override
    public void showGameList(List<Game> games, boolean isRefresh) {
        hideLoading();
        swipeRefreshLayout.setRefreshing(false);
        
        if (games == null || games.isEmpty()) {
            showEmptyGameList();
            return;
        }

        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewGames.setVisibility(View.VISIBLE);
        
        if (isRefresh) {
            gameListAdapter.setGames(games);
        } else {
            gameListAdapter.addGames(games);
        }
    }

    @Override
    public void showLoadMoreGames(List<Game> games) {
        isLoadingMore = false;
        if (games != null && !games.isEmpty()) {
            gameListAdapter.addGames(games);
        }
    }

    @Override
    public void showNoMoreGames() {
        isLoadingMore = false;
        gameListAdapter.setHasMoreData(false);
        showMessage(getString(R.string.no_more_games));
    }

    @Override
    public void showEmptyGameList() {
        hideLoading();
        swipeRefreshLayout.setRefreshing(false);
        recyclerViewGames.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToGameDetail(Game game) {
        Intent intent = GameDetailActivity.newIntent(getContext(), game);
        startActivity(intent);
    }

    @Override
    public void showGameCategories(List<String> categories) {
        tabLayoutCategories.removeAllTabs();
        
        // 添加"全部"分类
        TabLayout.Tab allTab = tabLayoutCategories.newTab().setText(getString(R.string.category_all));
        tabLayoutCategories.addTab(allTab);
        
        // 添加其他分类
        for (String category : categories) {
            TabLayout.Tab tab = tabLayoutCategories.newTab().setText(category);
            tabLayoutCategories.addTab(tab);
        }
        
        // 默认选中第一个tab
        if (tabLayoutCategories.getTabCount() > 0) {
            tabLayoutCategories.selectTab(tabLayoutCategories.getTabAt(0));
        }
    }

    @Override
    public void updateGameFavoriteStatus(String gameId, boolean isFavorited) {
        gameListAdapter.updateGameFavoriteStatus(gameId, isFavorited);
        String message = isFavorited ? "已添加到收藏" : "已取消收藏";
        showMessage(message);
    }

    @Override
    public void showLoading() {
        if (gameListAdapter.getItemCount() == 0) {
            // Show skeleton screen for better UX
            SkeletonUtil.showSkeleton(recyclerViewGames);
            stateViewHolder.showLoading("加载游戏列表...");
        } else {
            // Show pull-to-refresh indicator for existing content
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
        isLoadingMore = false;
        
        // Hide skeleton screen
        SkeletonUtil.hideSkeleton(recyclerViewGames);
        
        // Check if we have data to show
        boolean hasData = gameListAdapter.getItemCount() > 0;
        if (hasData) {
            stateViewHolder.showSuccess();
        } else {
            stateViewHolder.showEmpty("暂无游戏", "还没有找到相关游戏", "刷新");
        }
    }

    @Override
    public void showError(String message) {
        swipeRefreshLayout.setRefreshing(false);
        isLoadingMore = false;
        
        // Hide skeleton screen
        SkeletonUtil.hideSkeleton(recyclerViewGames);
        
        // Show network-aware error message
        String errorMessage = LoadingStateUtil.getNetworkAwareErrorMessage(requireContext(), message);
        
        if (gameListAdapter.getItemCount() == 0) {
            // Show full-screen error state
            if (!networkStatusUtil.isNetworkConnected()) {
                stateViewHolder.showNoNetwork();
            } else {
                stateViewHolder.showError("加载失败", errorMessage);
            }
        } else {
            // Show snackbar for existing content
            showMessage(errorMessage);
        }
    }

    private void showMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
        
        // Clean up network monitoring
        if (networkStatusUtil != null) {
            networkStatusUtil.removeNetworkStatusListener(null); // Remove all listeners for this fragment
        }
    }
}