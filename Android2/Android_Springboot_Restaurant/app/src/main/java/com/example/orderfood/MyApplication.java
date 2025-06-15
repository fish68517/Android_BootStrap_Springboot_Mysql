package com.example.orderfood;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.orderfood.model.Store;
import com.example.orderfood.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {
    public static Context mContext;
    private static User curUser ;
    public static Store selectedStore;

    public static void saveUser(User user) {
        curUser = user;

    }

    public static User getCurUser() {
        if (curUser == null) {
            curUser = new User();
            curUser.setNickname("张三");
            curUser.setPassword("123456");
            curUser.setPhone("13800138000");
            curUser.setUserId(1);
        }
        return curUser;
    }

    public static void clearCurUser() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveStore(Store store) {

        selectedStore = store;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;



    }

}
