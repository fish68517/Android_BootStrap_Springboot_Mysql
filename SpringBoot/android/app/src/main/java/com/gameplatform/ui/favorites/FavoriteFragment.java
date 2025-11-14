package com.gameplatform.ui.favorites;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gameplatform.R;
import com.gameplatform.base.BaseFragment;
import com.gameplatform.contract.FavoriteContract;
import com.gameplatform.model.Game;
import com.gameplatform.presenter.FavoritePresenter;
import com.gameplatform.ui.games.GameDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏列表Fragment
 * 实现收藏游戏的展示、管理和用户交互
 * Requirements: 4.3, 4.4, 4.5
 */
public class FavoriteFragment extends BaseFragment implements FavoriteContract.View {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewFavorites;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBarLoading;
    private MaterialButton btnDiscoverGames;

    private FavoriteAdapter favoriteAdapter;
    private FavoritePresenter presenter;
    private List<Game> favoriteGames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    protected void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerViewFavorites = view.findViewById(R.id.recycler_view_favorites);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBarLoading = view.findViewById(R.id.progress_bar_loading);
        btnDiscoverGames = view.findViewById(R.id.btn_discover_games);

        setupRecyclerView();
        setupSwipeRefresh();
        setupClickListeners();
    }

    @Override
    protected void initData() {
        presenter = new FavoritePresenter(this);
        favoriteGames = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favoriteGames);
        
        favoriteAdapter.setOnItemClickListener(game -> presenter.onGameItemClick(game));
        favoriteAdapter.setOnRemoveFavoriteClickListener(game -> presenter.onRemoveFavoriteClick(game));
        
        recyclerViewFavorites.setAdapter(favoriteAdapter);
        
        // 加载收藏数据
        presenter.loadFavoriteGames();
    }

    private void setupRecyclerView() {
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavorites.setHasFixedSize(true);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_color,
                R.color.secondary_color
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (presenter != null) {
                presenter.refreshFavoriteGames();
            }
        });
    }

    private void setupClickListeners() {
        btnDiscoverGames.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onDiscoverGamesClick();
            }
        });
    }

    // FavoriteContract.View 接口实现

    @Override
    public void showFavoriteGames(List<Game> games) {
        favoriteGames.clear();
        favoriteGames.addAll(games);
        favoriteAdapter.notifyDataSetChanged();
        
        recyclerViewFavorites.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyFavoriteList() {
        favoriteGames.clear();
        favoriteAdapter.notifyDataSetChanged();
        
        recyclerViewFavorites.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameRemovedFromFavorites(String gameId) {
        favoriteAdapter.removeGame(gameId);
        
        // 如果移除后列表为空，显示空状态
        if (favoriteAdapter.getItemCount() == 0) {
            showEmptyFavoriteList();
        }
    }

    @Override
    public void navigateToGameDetail(Game game) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), GameDetailActivity.class);
            intent.putExtra("game_id", game.getGameId());
            intent.putExtra("game_name", game.getName());
            startActivity(intent);
        }
    }

    @Override
    public void navigateToGameList() {
        // 切换到游戏列表Tab
        if (fragmentInteractionListener != null) {
            Bundle data = new Bundle();
            data.putString("target_tab", "game_list");
            fragmentInteractionListener.onFragmentInteraction("navigate_to_tab", data);
        }
    }

    @Override
    public void showRemoveFavoriteConfirmation(Game game) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.remove_favorite_title)
                .setMessage(getString(R.string.remove_favorite_message))
                .setPositiveButton(R.string.remove_favorite_confirm, (dialog, which) -> {
                    if (presenter != null) {
                        presenter.confirmRemoveFavorite(game);
                    }
                })
                .setNegativeButton(R.string.remove_favorite_cancel, null)
                .show();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            progressBarLoading.setVisibility(View.VISIBLE);
        } else {
            progressBarLoading.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showMessage(String message) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.showMessage(message);
        }
    }

    @Override
    public void showError(String error) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.showError(error);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从游戏详情页返回时刷新收藏状态
        if (presenter != null && favoriteAdapter.getItemCount() > 0) {
            presenter.refreshFavoriteGames();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}