package com.graduation.service.impl;

import com.graduation.entity.Comments;
import com.graduation.mapper.CommentsMapper;
import com.graduation.service.CommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 游戏评论表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

}
