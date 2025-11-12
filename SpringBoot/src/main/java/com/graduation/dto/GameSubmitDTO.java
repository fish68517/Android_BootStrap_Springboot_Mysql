package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 游戏提交DTO
 * 用于接收游戏发布和编辑请求数据
 */
@Data
public class GameSubmitDTO {
    
    /**
     * 游戏标题
     */
    @NotBlank(message = "游戏标题不能为空")
    @Size(max = 200, message = "游戏标题长度不能超过200个字符")
    private String title;
    
    /**
     * 游戏描述
     */
    @NotBlank(message = "游戏描述不能为空")
    @Size(max = 2000, message = "游戏描述长度不能超过2000个字符")
    private String description;
    
    /**
     * 封面图片URL
     */
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImageUrl;
    
    /**
     * 其他图片URLs（逗号分隔）
     */
    @Size(max = 2000, message = "其他图片URLs长度不能超过2000个字符")
    private String otherImageUrls;
}
