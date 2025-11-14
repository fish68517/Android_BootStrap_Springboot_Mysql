package com.gameplatform.model;

/**
 * Comment entity class representing comment content and like information
 * Requirements: 5.1, 5.2, 5.3, 5.4
 */
public class Comment {
    private String commentId;
    private String gameId;
    private String userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private long createTime;

    public Comment() {}

    public Comment(String commentId, String gameId, String userId, String userNickname,
                   String userAvatar, String content, long createTime) {
        this.commentId = commentId;
        this.gameId = gameId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userAvatar = userAvatar;
        this.content = content;
        this.createTime = createTime;
        this.likeCount = 0;
        this.isLiked = false;
    }

    // Getters and Setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", gameId='" + gameId + '\'' +
                ", userId='" + userId + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", content='" + content + '\'' +
                ", likeCount=" + likeCount +
                ", isLiked=" + isLiked +
                ", createTime=" + createTime +
                '}';
    }
}