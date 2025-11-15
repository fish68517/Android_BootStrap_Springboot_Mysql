package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.adapter.GameAdapter;
import com.archive.app.model.entity.Game;
import com.archive.app.view.activity.GameDetailActivity;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavouriteFragment extends Fragment implements GameAdapter.OnGameClickListener {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorites;

    private ApiService apiService;
    private MyApplication myApplication;
    private GameAdapter gameAdapter;
    private List<Game> favoriteList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_favourite, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvEmptyFavorites = view.findViewById(R.id.tv_empty_favorites);

        apiService = RetrofitClient.getMainApiService(); //


        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次返回该页面时都刷新
        loadFavorites();
    }

    private void setupRecyclerView() {
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        gameAdapter = new GameAdapter(getContext(), favoriteList, this);
        rvFavorites.setAdapter(gameAdapter);
    }

    private void loadFavorites() {


        int userId = myApplication.getUser().getUserId();
        apiService.getMyFavorites(userId).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteList = response.body();
                    gameAdapter.setGames(favoriteList);

                    if (favoriteList.isEmpty()) {
                        tvEmptyFavorites.setVisibility(View.VISIBLE);
                        rvFavorites.setVisibility(View.GONE);
                    } else {
                        tvEmptyFavorites.setVisibility(View.GONE);
                        rvFavorites.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "加载收藏失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGameClick(Game game) {
        Intent intent = new Intent(getContext(), GameDetailActivity.class);
        intent.putExtra("GAME_ID", game.getGameId());
        startActivity(intent);
        // TODO: 跳转到游戏详情页
    }
}