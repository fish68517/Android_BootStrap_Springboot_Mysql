package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.adapter.CategoryAdapter;
import com.archive.app.adapter.GameAdapter;
import com.archive.app.model.GameCategory;
import com.archive.app.model.entity.Game;
import com.archive.app.view.activity.GameDetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, GameAdapter.OnGameClickListener {

    private RecyclerView rvCategories, rvGames;
    private SearchView searchView;

    private ApiService apiService;
    private CategoryAdapter categoryAdapter;
    private GameAdapter gameAdapter;

    private List<GameCategory> categoryList = new ArrayList<>();
    private List<Game> gameList = new ArrayList<>();

    private String currentCategory = "全部";
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategories = view.findViewById(R.id.rv_categories);
        rvGames = view.findViewById(R.id.rv_games);
        searchView = view.findViewById(R.id.search_view);

        apiService = RetrofitClient.getMainApiService(); //

        setupCategories();
        setupGames();
        setupSearch();

        loadCategories();
        loadGames();

        return view;
    }

    private void setupCategories() {
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // 添加一个默认的 "全部"
        categoryList.add(new GameCategory("全部"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupGames() {
        rvGames.setLayoutManager(new LinearLayoutManager(getContext()));
        gameAdapter = new GameAdapter(getContext(), gameList, this);
        rvGames.setAdapter(gameAdapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户提交搜索时
                currentQuery = query;
                loadGames();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当搜索文本变化时 (可选)
                if (newText.isEmpty()) {
                    currentQuery = "";
                    loadGames();
                }
                return true;
            }
        });
    }

    private void loadCategories() {
       /* // 假设 /api/game/categories 返回 "RPG", "Strategy" 等
        apiService.getGameCategories().enqueue(new Callback<List<GameCategory>>() {
            @Override
            public void onResponse(Call<List<GameCategory>> call, Response<List<GameCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<GameCategory>> call, Throwable t) {
                // 加载分类失败 (非关键)
            }
        });*/
        categoryList.clear();
        categoryList.add(new GameCategory("全部"));

        categoryList.add(new GameCategory("动作类"));
        categoryList.add(new GameCategory("角色扮演"));
        categoryList.add(new GameCategory("策略类"));
        categoryList.add(new GameCategory("竞技类"));
        categoryList.add(new GameCategory("卡牌类"));
    }

    private void loadGames() {
        String categoryToSend = currentCategory.equals("全部") ? null : currentCategory;
        String queryToSend = currentQuery.isEmpty() ? null : currentQuery;

        apiService.getGames(categoryToSend, queryToSend).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList = response.body();
                    gameAdapter.setGames(gameList);
                } else {
                    Toast.makeText(getContext(), "加载游戏失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(GameCategory category) {
        // 点击分类时
        currentCategory = category.getName();
        loadGames();
    }

    @Override
    public void onGameClick(Game game) {
        // 点击游戏卡片时
        Toast.makeText(getContext(), "点击了: " + game.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: 跳转到游戏详情页
         Intent intent = new Intent(getActivity(), GameDetailActivity.class);
         intent.putExtra("GAME_ID", game.getGameId());
         startActivity(intent);
    }
}