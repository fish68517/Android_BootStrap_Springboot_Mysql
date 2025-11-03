package com.graduation.controller;

import com.graduation.entity.Preferences;
import com.graduation.service.PreferencesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 偏好定义表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/preferences")
public class PreferencesController extends BaseController<PreferencesService, Preferences> {

}
