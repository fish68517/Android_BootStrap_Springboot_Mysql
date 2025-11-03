package com.graduation.controller;

import com.graduation.entity.Games;
import com.graduation.service.GamesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 游戏信息表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/games")
public class GamesController extends BaseController<GamesService, Games> {

}
