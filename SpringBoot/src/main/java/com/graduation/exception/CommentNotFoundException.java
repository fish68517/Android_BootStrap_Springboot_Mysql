package com.graduation.exception;

/**
 * 评论未找到异常
 * 当查询的评论不存在时抛出
 */
public class CommentNotFoundException extends BusinessException {
    
    public CommentNotFoundException(String message) {
        super("COMMENT_NOT_FOUND", message);
    }
    
    public CommentNotFoundException(Integer commentId) {
        super("COMMENT_NOT_FOUND", "评论不存在: ID=" + commentId);
    }
}
