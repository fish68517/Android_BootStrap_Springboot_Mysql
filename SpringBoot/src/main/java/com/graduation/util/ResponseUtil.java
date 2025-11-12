package com.graduation.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应工具类
 * 用于构建标准化的API响应格式
 */
public class ResponseUtil {
    
    /**
     * 成功响应
     * @return 响应Map
     */
    public static Map<String, Object> success() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "操作成功");
        return response;
    }
    
    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @return 响应Map
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "操作成功");
        response.put("data", data);
        return response;
    }
    
    /**
     * 成功响应（带消息和数据）
     * @param message 响应消息
     * @param data 响应数据
     * @return 响应Map
     */
    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
    
    /**
     * 失败响应
     * @param message 错误消息
     * @return 响应Map
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    /**
     * 失败响应（带错误码）
     * @param code 错误码
     * @param message 错误消息
     * @return 响应Map
     */
    public static Map<String, Object> error(String code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", code);
        response.put("message", message);
        return response;
    }
}
