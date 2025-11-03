package com.graduation.controller;

import com.graduation.service.CommentLikesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 评论点赞表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/commentLikes")
public class CommentLikesController extends BaseController<CommentLikesService, com.graduation.entity.CommentLikes> {

}
