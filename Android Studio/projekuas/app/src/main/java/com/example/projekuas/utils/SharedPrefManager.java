package com.example.projekuas.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "projekuas_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_IMAGE = "profile_image";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Menyimpan data saat login sukses
    public void saveUser(int userId, String username) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // Mengecek apakah user sudah login
    public boolean isLoggedIn() {
        return sharedPreferences.getInt(KEY_USER_ID, -1) != -1;
    }

    // Mengambil data user_id
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Mengambil data username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Menghapus sesi saat Logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
    public void saveProfileImage(String imageUri) {
        editor.putString(KEY_PROFILE_IMAGE, imageUri);
        editor.apply();
    }
    public String getProfileImage() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE, null);
    }
}