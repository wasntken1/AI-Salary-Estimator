package com.example.projekuas.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;

import com.example.projekuas.R;
import com.example.projekuas.ui.fragment.AboutFragment;
import com.example.projekuas.ui.fragment.HistoryFragment;
import com.example.projekuas.ui.fragment.PredictFragment;
import com.example.projekuas.ui.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Mengatur listener jika menu bawah ditekan
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_predict) {
                selectedFragment = new PredictFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_about) {
                selectedFragment = new AboutFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            // Mengganti fragment di dalam wadah
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Menampilkan PredictFragment sebagai halaman pertama (default)
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_predict);
        }
    }
}