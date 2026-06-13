package com.example.projekuas.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.projekuas.R;
import com.example.projekuas.utils.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Memberikan delay selama 2.5 detik
        new Handler().postDelayed(() -> {
            SharedPrefManager prefManager = new SharedPrefManager(SplashActivity.this);

            // Logika pengecekan sesi login
            if (prefManager.isLoggedIn()) {
                // Jika sudah login, langsung ke halaman utama
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Jika belum, arahkan ke halaman Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish(); // Menutup SplashActivity agar tidak bisa di-back
        }, 2500);
    }
}