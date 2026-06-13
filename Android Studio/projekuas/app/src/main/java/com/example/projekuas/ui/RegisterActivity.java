package com.example.projekuas.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projekuas.R;
import com.example.projekuas.api.ApiClient;
import com.example.projekuas.api.ApiService;
import com.example.projekuas.api.DbClient;
import com.example.projekuas.model.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Inisialisasi Retrofit khusus Database (XAMPP)
        apiService = DbClient.getClient().create(ApiService.class);

        // Tombol Daftar
        btnRegister.setOnClickListener(v -> prosesRegister());

        // Tulisan kembali ke Login
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void prosesRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Memproses...");

        // Menembak API register.php
        apiService.register(username, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Daftar Sekarang");

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse res = response.body();
                    Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    if (res.getStatus().equals("success")) {
                        finish(); // Kembali ke halaman Login jika sukses
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Daftar Sekarang");
                Toast.makeText(RegisterActivity.this, "Koneksi ke Database Gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}