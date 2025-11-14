package com.gameplatform.ui.games;

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
import com.gameplatform.util.AnimationUtil;

import java.util.List;

/**
 * 游戏列表适配器
 * 实现游戏列表项展示、图片加载、点击事件处理和分页加载
 * Requirements: 3.1, 3.3, 3.4
 */
public class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GAME_ITEM = 0;
    private static final int TYPE_LOADING = 1;

    private List<Game> games;
    private OnItemClickListener onItemClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean hasMoreData = true;

    public interface OnItemClickListener {
        void onItemClick(Game game);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Game game);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public GameListAdapter(List<Game> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_game, parent, false);
            return new GameViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameViewHolder) {
            Game game = games.get(position);
            ((GameViewHolder) holder).bind(game);
            
            // Add entrance animation for new items
            AnimationUtil.animateRecyclerViewItem(holder.itemView, position);
            
            // 触发加载更多
            if (position == games.size() - 1 && hasMoreData && onLoadMoreListener != null) {
                onLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public int getItemCount() {
        return games.size() + (hasMoreData ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == games.size() && hasMoreData) {
            return TYPE_LOADING;
        }
        return TYPE_GAME_ITEM;
    }

    public void setGames(List<Game> games) {
        this.games = games;
        hasMoreData = true;
        notifyDataSetChanged();
    }

    public void addGames(List<Game> newGames) {
        int startPosition = games.size();
        games.addAll(newGames);
        notifyItemRangeInserted(startPosition, newGames.size());
    }

    public void updateGameFavoriteStatus(String gameId, boolean isFavorited) {
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.getGameId().equals(gameId)) {
                game.setFavorited(isFavorited);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setHasMoreData(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    class GameViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGameIcon;
        private TextView tvGameName;
        private RatingBar ratingBarGame;
        private TextView tvRatingText;
        private TextView tvGameDescription;
        private TextView tvCommentCount;
        private ImageView ivFavorite;

        public GameViewHolder(@NonNull View itemView) {
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
                    // Add click animation
                    AnimationUtil.bounceClick(itemView);
                    onItemClickListener.onItemClick(games.get(position));
                }
            });

            ivFavorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onFavoriteClickListener != null) {
                    Game game = games.get(position);
                    // Animate favorite button click
                    AnimationUtil.animateFavoriteToggle(ivFavorite, !game.isFavorited());
                    onFavoriteClickListener.onFavoriteClick(game);
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

            // 设置收藏状态
            updateFavoriteIcon(game.isFavorited());

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

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}