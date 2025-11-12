package com.graduation.mapper;

import com.graduation.entity.Preferences;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 偏好定义表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface PreferencesMapper extends BaseMapper<Preferences> {
    // 基础CRUD方法由BaseMapper提供
}
