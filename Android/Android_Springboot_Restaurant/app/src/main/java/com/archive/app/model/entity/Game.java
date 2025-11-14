package com.archive.app.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * 游戏实体
 * 对应: games 表
 */
public class Game implements Serializable {


    private int gameId;


    private int publisherId;


    private String title;


    private String description;


    private String coverImageUrl;


    private String otherImageUrls;


    private String status;


    private Date submittedAt;


    private Integer reviewedByAdminId;


    private Date reviewedAt;


    private String category;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", publisherId=" + publisherId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", otherImageUrls='" + otherImageUrls + '\'' +
                ", status='" + status + '\'' +
                ", submittedAt=" + submittedAt +
                ", reviewedByAdminId=" + reviewedByAdminId +
                ", reviewedAt=" + reviewedAt +
                ", category='" + category + '\'' +
                '}';
    }
}