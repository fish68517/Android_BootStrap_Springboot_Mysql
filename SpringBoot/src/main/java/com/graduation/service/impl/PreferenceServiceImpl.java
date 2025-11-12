package com.graduation.service.impl;

import com.graduation.entity.Preferences;
import com.graduation.entity.UserPreferences;
import com.graduation.mapper.PreferencesMapper;
import com.graduation.mapper.UserPreferencesMapper;
import com.graduation.service.PreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 偏好管理服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-11-12
 */
@Service
public class PreferenceServiceImpl implements PreferenceService {

    @Autowired
    private PreferencesMapper preferencesMapper;

    @Autowired
    private UserPreferencesMapper userPreferencesMapper;

    /**
     * 获取所有偏好选项
     * @return 所有偏好选项列表
     */
    @Override
    public List<Preferences> getAllPreferences() {
        return preferencesMapper.selectList(null);
    }

    /**
     * 设置用户偏好（批量插入）
     * 先删除用户现有的所有偏好，然后批量插入新的偏好
     * @param userId 用户ID
     * @param preferenceIds 偏好ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserPreferences(Integer userId, List<Integer> preferenceIds) {
        // 先删除用户现有的所有偏好
        userPreferencesMapper.deleteByUserId(userId);

        // 如果偏好列表不为空，批量插入新的偏好
        if (preferenceIds != null && !preferenceIds.isEmpty()) {
            for (Integer preferenceId : preferenceIds) {
                UserPreferences userPreference = new UserPreferences();
                userPreference.setUserId(userId);
                userPreference.setPreferenceId(preferenceId);
                userPreferencesMapper.insert(userPreference);
            }
        }
    }

    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户的偏好列表
     */
    @Override
    public List<Preferences> getUserPreferences(Integer userId) {
        // 查询用户的偏好关联记录
        List<UserPreferences> userPreferencesList = userPreferencesMapper.selectByUserId(userId);

        // 如果用户没有设置偏好，返回空列表
        if (userPreferencesList == null || userPreferencesList.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取偏好ID列表
        List<Integer> preferenceIds = userPreferencesList.stream()
                .map(UserPreferences::getPreferenceId)
                .collect(Collectors.toList());

        // 查询偏好详细信息
        return preferencesMapper.selectBatchIds(preferenceIds);
    }
}
