package com.archive.app;

import android.content.Context;
import android.content.Intent;

import com.archive.app.model.entity.User;
import com.archive.app.view.activity.LoginActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyApplication extends android.app.Application{


    public static User curUser;

    private static final String TAG = "MyApplication";
    private Context context;

    private ApiService apiService = RetrofitClient.getMainApiService();
    public static List<User> users;


    public static void setUser(User user) {

        // save user to shared preferences or database or any other storage
        curUser = user;
    }


    public static User getUser() {
        return curUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your application here
        context = getApplicationContext();
        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                System.out.println("getAllUsers：" + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();

                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }


    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
