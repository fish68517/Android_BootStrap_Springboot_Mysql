package com.graduation.service.impl;

import com.graduation.entity.Games;
import com.graduation.mapper.GamesMapper;
import com.graduation.service.GamesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 游戏信息表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Service
public class GamesServiceImpl extends ServiceImpl<GamesMapper, Games> implements GamesService {

}
