package com.graduation.controller;

import com.graduation.service.RecommendationsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 管理员推荐表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/recommendations")
public class RecommendationsController extends BaseController<RecommendationsService, com.graduation.entity.Recommendations> {

}
