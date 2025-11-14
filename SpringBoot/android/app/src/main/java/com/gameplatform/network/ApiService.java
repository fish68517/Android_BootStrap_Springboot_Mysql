package com.gameplatform.network;

import com.gameplatform.dto.ApiResponse;
import com.gameplatform.dto.request.*;
import com.gameplatform.dto.response.*;
import com.gameplatform.model.Comment;
import com.gameplatform.model.Game;
import com.gameplatform.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * API service interface for network requests
 * Requirements: 1.1, 2.1, 3.2, 4.1, 5.1, 6.2
 */
public interface ApiService {
    
    // Authentication APIs
    @POST("auth/send-code")
    Call<ApiResponse<Void>> sendVerificationCode(@Body SendCodeRequest request);
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest request);
    
    // Game APIs
    @GET("games")
    Call<ApiResponse<GameListResponse>> getGameList(
            @Query("page") int page, 
            @Query("category") String category
    );
    
    @GET("games/{gameId}")
    Call<ApiResponse<Game>> getGameDetail(@Path("gameId") String gameId);
    
    @GET("games/categories")
    Call<ApiResponse<List<String>>> getGameCategories();
    
    // Favorite APIs
    @POST("favorites")
    Call<ApiResponse<Void>> addFavorite(@Body FavoriteRequest request);
    
    @DELETE("favorites/{gameId}")
    Call<ApiResponse<Void>> removeFavorite(@Path("gameId") String gameId);
    
    @GET("favorites")
    Call<ApiResponse<List<Game>>> getFavoriteList();
    
    // Comment APIs
    @GET("games/{gameId}/comments")
    Call<ApiResponse<List<Comment>>> getComments(@Path("gameId") String gameId);
    
    @POST("comments")
    Call<ApiResponse<Comment>> addComment(@Body CommentRequest request);
    
    @POST("comments/{commentId}/like")
    Call<ApiResponse<Void>> likeComment(@Path("commentId") String commentId);
    
    @DELETE("comments/{commentId}/like")
    Call<ApiResponse<Void>> unlikeComment(@Path("commentId") String commentId);
    
    // User Profile APIs
    @GET("user/profile")
    Call<ApiResponse<User>> getUserProfile();
    
    @PUT("user/profile")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);
    
    @Multipart
    @POST("user/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part avatar);
}