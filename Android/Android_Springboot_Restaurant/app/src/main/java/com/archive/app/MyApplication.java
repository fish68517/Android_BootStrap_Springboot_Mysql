package com.archive.app;

import android.content.Context;
import android.content.Intent;

import com.archive.app.model.entity.User;
import com.archive.app.view.activity.LoginActivity;


public class MyApplication extends android.app.Application{


    public static User curUser;

    private static final String TAG = "MyApplication";
    private Context context;



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



    }


    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
