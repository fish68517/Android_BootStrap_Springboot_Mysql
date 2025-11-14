package com.archive.app;

import android.content.Context;


import com.archive.app.model.User;

public class MyApplication extends android.app.Application{


    public static User curUser;

    private static final String TAG = "MyApplication";
    private Context context;



    public static void setUser(User user) {

        // save user to shared preferences or database or any other storage
        curUser = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your application here
        context = getApplicationContext();



    }



}
