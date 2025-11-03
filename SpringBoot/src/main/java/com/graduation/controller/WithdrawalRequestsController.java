package com.graduation.controller;

import com.graduation.service.WithdrawalRequestsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

/**
 * <p>
 * 游戏撤回审核表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/withdrawalRequests")
public class WithdrawalRequestsController extends BaseController<WithdrawalRequestsService, com.graduation.entity.WithdrawalRequests> {

}
