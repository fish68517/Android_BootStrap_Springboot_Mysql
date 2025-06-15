package com.example.orderfood.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "app_pref";
    private static final String KEY_STORE_ID = "selected_store_id";
    private static final String KEY_STORE_NAME = "selected_store_name";

    public static void saveSelectedStore(Context context, int storeId, String storeName) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putInt(KEY_STORE_ID, storeId)
                .putString(KEY_STORE_NAME, storeName)
                .apply();
    }

    public static int getSelectedStoreId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_STORE_ID, -1);
    }

    public static String getSelectedStoreName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_STORE_NAME, "");
    }

    public static void clearSelectedStore(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .remove(KEY_STORE_ID)
                .remove(KEY_STORE_NAME)
                .apply();
    }
} 