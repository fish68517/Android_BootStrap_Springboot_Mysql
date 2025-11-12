package com.graduation.exception;

/**
 * 游戏未找到异常
 * 当查询的游戏不存在时抛出
 */
public class GameNotFoundException extends BusinessException {
    
    public GameNotFoundException(String message) {
        super("GAME_NOT_FOUND", message);
    }
    
    public GameNotFoundException(Integer gameId) {
        super("GAME_NOT_FOUND", "游戏不存在: ID=" + gameId);
    }
}
