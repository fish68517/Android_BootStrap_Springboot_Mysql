package com.graduation.controller;

import com.graduation.entity.Comments;
import com.graduation.service.CommentsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 游戏评论表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/comments")
public class CommentsController extends BaseController<CommentsService, Comments> {

}
