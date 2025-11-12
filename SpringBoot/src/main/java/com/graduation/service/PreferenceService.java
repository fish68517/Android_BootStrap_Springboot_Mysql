package com.graduation.service;

import com.graduation.entity.Preferences;
import java.util.List;

/**
 * <p>
 * 偏好管理服务类 - 提供用户偏好管理的业务逻辑
 * </p>
 *
 * @author 张三
 * @since 2025-11-12
 */
public interface PreferenceService {

    /**
     * 获取所有偏好选项
     * @return 所有偏好选项列表
     */
    List<Preferences> getAllPreferences();

    /**
     * 设置用户偏好（批量插入）
     * @param userId 用户ID
     * @param preferenceIds 偏好ID列表
     */
    void setUserPreferences(Integer userId, List<Integer> preferenceIds);

    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户的偏好列表
     */
    List<Preferences> getUserPreferences(Integer userId);
}
