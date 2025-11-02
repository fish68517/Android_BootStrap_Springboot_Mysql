package com.archive.app;

import com.archive.app.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MainApiService {

    // --- User Endpoints ---
    @POST("/user")
    Call<Boolean> createUser(@Body User user);

    @GET("/api/users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    @GET("/api/users/username/{username}")
    Call<User> getUserByUsername(@Path("username") String username);



    @DELETE("/api/addresses/{addressId}")
    Call<Void> deleteAddress(@Path("addressId") Long addressId);



} 