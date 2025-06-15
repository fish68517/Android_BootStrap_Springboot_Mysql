package com.example.orderfood.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static UserManager instance;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public int getCurrentUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public String getCurrentUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }


    public boolean isLoggedIn() {
        return getCurrentUserId() != -1;
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
} 