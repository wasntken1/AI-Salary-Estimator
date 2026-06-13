package com.example.projekuas.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DbClient {
    // URL dasar untuk menembak ke XAMPP
    // Pake 10.0.2.2 kalo pakai Emulator Android Studio
    // Pake IP Laptop (cth 192.168.1.x) kalo pakai HP Asli
    private static final String BASE_URL = "http://10.11.7.201/api_projekuas/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}