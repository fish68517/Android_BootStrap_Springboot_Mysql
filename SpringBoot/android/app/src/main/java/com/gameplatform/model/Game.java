package com.gameplatform.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Game entity class representing game information and favorite status
 * Requirements: 3.1, 4.1, 5.1
 */
public class Game implements Parcelable {
    private String gameId;
    private String name;
    private String icon;
    private String coverImage;
    private String description;
    private float rating;
    private String category;
    private boolean isFavorited;
    private int commentCount;

    public Game() {}

    public Game(String gameId, String name, String icon, String coverImage, String description, 
                float rating, String category) {
        this.gameId = gameId;
        this.name = name;
        this.icon = icon;
        this.coverImage = coverImage;
        this.description = description;
        this.rating = rating;
        this.category = category;
        this.isFavorited = false;
        this.commentCount = 0;
    }
    
    protected Game(Parcel in) {
        gameId = in.readString();
        name = in.readString();
        icon = in.readString();
        coverImage = in.readString();
        description = in.readString();
        rating = in.readFloat();
        category = in.readString();
        isFavorited = in.readByte() != 0;
        commentCount = in.readInt();
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameId);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(coverImage);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeString(category);
        dest.writeByte((byte) (isFavorited ? 1 : 0));
        dest.writeInt(commentCount);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }
        
        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
    
    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                ", category='" + category + '\'' +
                ", isFavorited=" + isFavorited +
                ", commentCount=" + commentCount +
                '}';
    }
}