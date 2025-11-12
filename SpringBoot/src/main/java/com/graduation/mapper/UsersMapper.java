package com.graduation.mapper;

import com.graduation.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Users selectByUsername(@Param("username") String username);
}
