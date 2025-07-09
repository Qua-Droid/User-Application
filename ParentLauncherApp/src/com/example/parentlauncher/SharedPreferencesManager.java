package com.example.parentlauncher;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "ParentLauncherPrefs";
    private static final String KEY_CURRENT_CHILD_USER_ID = "current_child_user_id";
    private static final String KEY_PROFILE_TYPE_PREFIX = "profile_type_";

    public static void saveCurrentChildUserId(Context context, int userId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_CURRENT_CHILD_USER_ID, userId);
        editor.apply();
    }

    public static int getCurrentChildUserId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(KEY_CURRENT_CHILD_USER_ID, -1); // -1 is default if not found
    }

    public static void clearCurrentChildUserId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_CURRENT_CHILD_USER_ID);
        editor.apply();
    }

    public static void saveProfileType(Context context, int userId, String profileType) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PROFILE_TYPE_PREFIX + userId, profileType);
        editor.apply();
    }

    public static String getProfileType(Context context, int userId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_PROFILE_TYPE_PREFIX + userId, "boy"); // Default to boy if not found
    }

    public static void clearProfileType(Context context, int userId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_PROFILE_TYPE_PREFIX + userId);
        editor.apply();
    }
}

