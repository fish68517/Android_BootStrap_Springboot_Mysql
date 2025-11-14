package com.archive.app.model.dto;

/**
 * 提交撤回请求 DTO
 * 对应: GameController.java
 */
public class WithdrawalSubmitDTO {

    private Integer gameId;
    private String reason;

    public WithdrawalSubmitDTO(Integer gameId, String reason) {
        this.gameId = gameId;
        this.reason = reason;
    }

    // Getters and Setters
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}