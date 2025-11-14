package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 游戏实体
 * 对应: games 表
 */
public class Game {

    @SerializedName("game_id")
    private int gameId;

    @SerializedName("publisher_id")
    private int publisherId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("cover_image_url")
    private String coverImageUrl;

    @SerializedName("other_image_urls")
    private String otherImageUrls;

    @SerializedName("status")
    private String status;

    @SerializedName("submitted_at")
    private Date submittedAt;

    @SerializedName("reviewed_by_admin_id")
    private Integer reviewedByAdminId;

    @SerializedName("reviewed_at")
    private Date reviewedAt;

    // Getters and Setters
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getOtherImageUrls() {
        return otherImageUrls;
    }

    public void setOtherImageUrls(String otherImageUrls) {
        this.otherImageUrls = otherImageUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getReviewedByAdminId() {
        return reviewedByAdminId;
    }

    public void setReviewedByAdminId(Integer reviewedByAdminId) {
        this.reviewedByAdminId = reviewedByAdminId;
    }

    public Date getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}