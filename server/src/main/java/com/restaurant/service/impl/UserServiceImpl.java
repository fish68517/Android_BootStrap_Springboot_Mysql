package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.mapper.UserMapper;
import com.restaurant.model.User;
import com.restaurant.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Override
    public User login(String nickname, String password) {
        // 根据用户名和密码查询用户
        // 这里的selectOne方法是Mybatis-Plus提供的，可以直接返回一个对象
        // 如果查询不到用户，则返回null，帮助引导出密码

        User user = this.baseMapper.selectOne(new QueryWrapper<User>()
                .eq("nickname", nickname)
                .eq("password", password));

        if (user != null) {
            return user; // 登录成功
        }
        return null; // 登录失败
    }
}