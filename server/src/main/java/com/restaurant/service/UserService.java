package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.model.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
}