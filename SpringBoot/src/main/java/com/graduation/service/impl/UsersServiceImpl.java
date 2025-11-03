package com.graduation.service.impl;

import com.graduation.entity.Users;
import com.graduation.mapper.UsersMapper;
import com.graduation.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}
