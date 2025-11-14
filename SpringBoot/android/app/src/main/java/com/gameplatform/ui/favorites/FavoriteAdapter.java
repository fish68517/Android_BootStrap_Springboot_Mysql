package com.gameplatform.ui.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.gameplatform.R;
import com.gameplatform.model.Game;

import java.util.List;

/**
 * 收藏游戏列表适配器
 * 复用游戏列表项布局，专门用于收藏页面
 * Requirements: 4.3, 4.4, 4.5
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Game> favoriteGames;
    private OnItemClickListener onItemClickListener;
    private OnRemoveFavoriteClickListener onRemoveFavoriteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Game game);
    }

    public interface OnRemoveFavoriteClickListener {
        void onRemoveFavoriteClick(Game game);
    }

    public FavoriteAdapter(List<Game> favoriteGames) {
        this.favoriteGames = favoriteGames;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Game game = favoriteGames.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return favoriteGames != null ? favoriteGames.size() : 0;
    }

    public void setFavoriteGames(List<Game> games) {
        this.favoriteGames = games;
        notifyDataSetChanged();
    }

    public void removeGame(String gameId) {
        if (favoriteGames != null) {
            for (int i = 0; i < favoriteGames.size(); i++) {
                if (favoriteGames.get(i).getGameId().equals(gameId)) {
                    favoriteGames.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnRemoveFavoriteClickListener(OnRemoveFavoriteClickListener listener) {
        this.onRemoveFavoriteClickListener = listener;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGameIcon;
        private TextView tvGameName;
        private RatingBar ratingBarGame;
        private TextView tvRatingText;
        private TextView tvGameDescription;
        private TextView tvCommentCount;
        private ImageView ivFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGameIcon = itemView.findViewById(R.id.iv_game_icon);
            tvGameName = itemView.findViewById(R.id.tv_game_name);
            ratingBarGame = itemView.findViewById(R.id.rating_bar_game);
            tvRatingText = itemView.findViewById(R.id.tv_rating_text);
            tvGameDescription = itemView.findViewById(R.id.tv_game_description);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(favoriteGames.get(position));
                }
            });

            // 收藏按钮点击事件 - 在收藏页面点击表示取消收藏
            ivFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onRemoveFavoriteClickListener != null) {
                    onRemoveFavoriteClickListener.onRemoveFavoriteClick(favoriteGames.get(position));
                }
            });
        }

        public void bind(Game game) {
            // 设置游戏名称
            tvGameName.setText(game.getName());

            // 设置游戏描述
            tvGameDescription.setText(game.getDescription());

            // 设置评分
            ratingBarGame.setRating(game.getRating());
            tvRatingText.setText(String.valueOf(game.getRating()));

            // 设置评论数量
            String commentText = itemView.getContext().getString(R.string.comment_count_format, game.getCommentCount());
            tvCommentCount.setText(commentText);

            // 在收藏页面，所有游戏都是已收藏状态
            updateFavoriteIcon(true);

            // 加载游戏图标
            loadGameIcon(game.getIcon());
        }

        private void updateFavoriteIcon(boolean isFavorited) {
            if (isFavorited) {
                ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
                ivFavorite.setColorFilter(itemView.getContext().getColor(R.color.error));
            } else {
                ivFavorite.setImageResource(R.drawable.ic_favorite_border);
                ivFavorite.setColorFilter(itemView.getContext().getColor(R.color.text_disabled));
            }
        }

        private void loadGameIcon(String iconUrl) {
            if (iconUrl != null && !iconUrl.isEmpty()) {
                RequestOptions options = new RequestOptions()
                        .transform(new RoundedCorners(
                                itemView.getContext().getResources().getDimensionPixelSize(R.dimen.card_corner_radius)))
                        .placeholder(R.drawable.bg_rounded_image)
                        .error(R.drawable.bg_rounded_image);

                Glide.with(itemView.getContext())
                        .load(iconUrl)
                        .apply(options)
                        .into(ivGameIcon);
            } else {
                ivGameIcon.setImageResource(R.drawable.bg_rounded_image);
            }
        }
    }
}