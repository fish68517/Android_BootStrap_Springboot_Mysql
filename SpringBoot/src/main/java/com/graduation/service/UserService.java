package com.graduation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.dto.UserLoginDTO;
import com.graduation.dto.UserRegisterDTO;
import com.graduation.entity.Users;
import com.graduation.exception.DuplicateUsernameException;
import com.graduation.exception.UnauthorizedException;
import com.graduation.exception.UserNotFoundException;
import com.graduation.mapper.UsersMapper;
import com.graduation.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务类
 * 处理用户注册、登录、资料管理等业务逻辑
 */
@Service
public class UserService {

    @Autowired
    private UsersMapper usersMapper;

    /**
     * 用户注册
     * 验证用户名唯一性，密码加密后保存
     * 
     * @param dto 注册信息
     * @return 注册成功的用户对象
     * @throws DuplicateUsernameException 用户名已存在
     */
    @Transactional
    public Users register(UserRegisterDTO dto) {
        // 验证用户名唯一性
        Users existingUser = usersMapper.selectByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new DuplicateUsernameException(dto.getUsername());
        }

        // 创建新用户
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(PasswordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setRole(dto.getRole() != null ? dto.getRole() :"user"); // 默认角色为普通用户
        user.setCreatedAt(LocalDateTime.now());

        // 保存到数据库
        usersMapper.insert(user);
        
        return user;
    }

    /**
     * 用户登录
     * 验证用户名和密码
     * 
     * @param dto 登录信息
     * @return 登录成功的用户对象
     * @throws UserNotFoundException 用户不存在
     * @throws UnauthorizedException 密码错误
     */
    public Users login(UserLoginDTO dto) {
        // 查询用户
        Users user = usersMapper.selectByUsername(dto.getUsername());
        // 打印
        System.out.println("用户名：" + user.getUsername());
        System.out.println("密码：" + dto.getPassword());
        System.out.println("角色：" + user.getRole());

        if (user == null) {
            throw new UserNotFoundException("username", dto.getUsername());
        }

        // 验证密码
        if (!PasswordUtil.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        // 验证角色
        if (!user.getRole().equals(dto.getRole())) {
            throw new UnauthorizedException("用户角色错误");
        }

        return user;
    }

    /**
     * 更新用户资料
     * 
     * @param userId 用户ID
     * @param updatedUser 更新的用户信息
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional
    public void updateProfile(Integer userId, Users updatedUser) {
        // 验证用户是否存在
        Users existingUser = usersMapper.selectById(userId);
        if (existingUser == null) {
            throw new UserNotFoundException(userId);
        }

        // 更新允许修改的字段
        if (updatedUser.getNickname() != null) {
            existingUser.setNickname(updatedUser.getNickname());
        }
        
        // 如果需要修改密码
        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(PasswordUtil.encode(updatedUser.getPasswordHash()));
        }

        usersMapper.updateById(existingUser);
    }

    /**
     * 删除用户
     * 级联删除关联数据（由数据库外键约束处理）
     * 
     * @param userId 用户ID
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional
    public void deleteUser(Integer userId) {
        // 验证用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        // 删除用户（级联删除由数据库外键约束处理）
        usersMapper.deleteById(userId);
    }

    /**
     * 查询所有用户（管理员功能）
     * 
     * @return 所有用户列表
     */
    public List<Users> getAllUsers() {
        return usersMapper.selectList(null);
    }

    /**
     * 根据ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象
     * @throws UserNotFoundException 用户不存在
     */
    public Users getUserById(Integer userId) {
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象
     * @throws UserNotFoundException 用户不存在
     */
    public Users getUserByUsername(String username) {
        Users user = usersMapper.selectByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("username", username);
        }
        return user;
    }
}
