package com.archive.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.model.entity.Comment;
import com.archive.app.model.entity.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载 item_comment.xml 布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // 获取当前位置的评论数据
        Comment comment = commentList.get(position);
        if (comment == null) {
            return;
        }

        // 绑定评论内容
        holder.tvCommentContent.setText(comment.getContent());

        // 绑定评论时间
        if (comment.getCreatedAt() != null) {
            holder.tvCommentDate.setText(dateFormat.format(comment.getCreatedAt()));
        }

        // 绑定用户信息 (用户名和头像)
        int userId = comment.getUserId();
        List<User> users = MyApplication.users;
        User user = null;
        for (User user0 : users) {
            if (user0.getUserId() == userId) {
                user = user0;
                break;
            }
        }
        if (user != null) {
            holder.tvUsername.setText(user.getUsername());

            // 使用 Glide 加载用户头像
            Glide.with(holder.itemView.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_person) // 建议您创建一个占位图
                    .error(R.drawable.ic_person)       // 加载失败时显示的图片
                    .transform(new CircleCrop()) // 将图片裁剪为圆形
                    .into(holder.ivUserAvatar);
        } else {
            // 如果没有用户信息，可以显示默认值
            holder.tvUsername.setText("匿名用户");
            holder.ivUserAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    /**
     * ViewHolder 类，用于缓存 item_comment.xml 中的视图
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUsername;
        TextView tvCommentDate;
        TextView tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_user_nickname);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_time);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
        }
    }
}
