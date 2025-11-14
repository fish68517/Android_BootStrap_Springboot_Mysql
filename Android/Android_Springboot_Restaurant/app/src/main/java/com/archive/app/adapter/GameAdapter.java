package com.archive.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.entity.Game;
import com.archive.app.util.ImageLoaderUtil; //
import com.archive.app.util.TextFormatter; //

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private Context context;
    private List<Game> gameList;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(Game game);
    }

    public GameAdapter(Context context, List<Game> gameList, OnGameClickListener listener) {
        this.context = context;
        this.gameList = gameList;
        this.listener = listener;
    }

    public void setGames(List<Game> newGameList) {
        this.gameList = newGameList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);

        holder.tvGameTitle.setText(game.getTitle());
        holder.tvGameCategory.setText(game.getCategory());
        // 截断描述
        holder.tvGameDescription.setMaxLines(2);
        holder.tvGameDescription.setText(game.getDescription());

        // 加载图片
        ImageLoaderUtil.loadImage(context, game.getCoverImageUrl(), holder.ivGameCover);

        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGameCover;
        TextView tvGameTitle, tvGameCategory, tvGameDescription;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGameCover = itemView.findViewById(R.id.iv_game_cover);
            tvGameTitle = itemView.findViewById(R.id.tv_game_title);
            tvGameCategory = itemView.findViewById(R.id.tv_game_category);
            tvGameDescription = itemView.findViewById(R.id.tv_game_description);
        }
    }
}