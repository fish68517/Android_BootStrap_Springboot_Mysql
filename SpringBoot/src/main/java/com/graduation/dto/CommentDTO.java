package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论DTO
 * 用于接收评论发布请求数据
 */
@Data
public class CommentDTO {
    
    /**
     * 游戏ID
     */
    @NotNull(message = "游戏ID不能为空")
    private Integer gameId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    private String content;
}
