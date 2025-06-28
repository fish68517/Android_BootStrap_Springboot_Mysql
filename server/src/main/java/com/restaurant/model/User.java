package com.restaurant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String gener;
} 