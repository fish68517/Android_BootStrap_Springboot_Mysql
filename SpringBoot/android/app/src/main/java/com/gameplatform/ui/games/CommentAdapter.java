package com.gameplatform.ui.games;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gameplatform.R;
import com.gameplatform.model.Comment;
import com.gameplatform.util.AnimationUtil;
import com.gameplatform.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    
    private List<Comment> comments = new ArrayList<>();
    private OnCommentLikeListener onCommentLikeListener;
    
    public interface OnCommentLikeListener {
        void onCommentLike(Comment comment, int position, boolean currentlyLiked);
    }
    
    public void setOnCommentLikeListener(OnCommentLikeListener listener) {
        this.onCommentLikeListener = listener;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments.clear();
        if (comments != null) {
            this.comments.addAll(comments);
        }
        notifyDataSetChanged();
    }
    
    public void addComment(Comment comment) {
        if (comment != null) {
            comments.add(0, comment); // Add to top
            notifyItemInserted(0);
        }
    }
    
    public void updateCommentLike(int position, boolean isLiked, int likeCountChange) {
        if (position >= 0 && position < comments.size()) {
            Comment comment = comments.get(position);
            comment.setLiked(isLiked);
            
            // Update like count based on the change
            int currentCount = comment.getLikeCount();
            int newCount = Math.max(0, currentCount + likeCountChange);
            comment.setLikeCount(newCount);
            
            notifyItemChanged(position);
        }
    }
    
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment, position);
        
        // Add entrance animation for comment items
        AnimationUtil.animateRecyclerViewItem(holder.itemView, position);
    }
    
    @Override
    public int getItemCount() {
        return comments.size();
    }
    
    class CommentViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView ivUserAvatar;
        private TextView tvUserNickname;
        private TextView tvCommentTime;
        private TextView tvCommentContent;
        private LinearLayout llLike;
        private ImageView ivLike;
        private TextView tvLikeCount;
        
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }
        
        private void initViews() {
            ivUserAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUserNickname = itemView.findViewById(R.id.tv_user_nickname);
            tvCommentTime = itemView.findViewById(R.id.tv_comment_time);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            llLike = itemView.findViewById(R.id.ll_like);
            ivLike = itemView.findViewById(R.id.iv_like);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
        }
        
        public void bind(Comment comment, int position) {
            // Load user avatar
            Glide.with(itemView.getContext())
                    .load(comment.getUserAvatar())
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(ivUserAvatar);
            
            tvUserNickname.setText(comment.getUserNickname());
            tvCommentTime.setText(TimeUtil.formatRelativeTime(comment.getCreateTime()));
            tvCommentContent.setText(comment.getContent());
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            
            // Update like button state
            updateLikeButton(comment.isLiked());
            
            // Set click listener for like button
            llLike.setOnClickListener(v -> {
                if (onCommentLikeListener != null) {
                    // Animate like button click
                    AnimationUtil.animateLikeButton(ivLike, !comment.isLiked());
                    onCommentLikeListener.onCommentLike(comment, position, comment.isLiked());
                }
            });
        }
        
        private void updateLikeButton(boolean isLiked) {
            if (isLiked) {
                ivLike.setImageResource(R.drawable.ic_thumb_up_filled);
                ivLike.setColorFilter(itemView.getContext().getColor(R.color.primary_color));
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.primary_color));
            } else {
                ivLike.setImageResource(R.drawable.ic_thumb_up_outline);
                ivLike.setColorFilter(itemView.getContext().getColor(R.color.text_medium_emphasis));
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.text_medium_emphasis));
            }
        }
    }
}