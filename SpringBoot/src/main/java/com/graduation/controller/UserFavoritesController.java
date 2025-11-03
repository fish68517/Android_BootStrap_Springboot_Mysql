package com.graduation.controller;

import com.graduation.service.UserFavoritesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 用户收藏表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/userFavorites")
public class UserFavoritesController extends BaseController<UserFavoritesService, com.graduation.entity.UserFavorites> {

}
