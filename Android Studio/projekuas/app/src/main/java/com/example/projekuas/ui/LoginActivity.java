package com.example.projekuas.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projekuas.R;
import com.example.projekuas.api.ApiService;
import com.example.projekuas.api.DbClient;
import com.example.projekuas.model.AuthResponse;
import com.example.projekuas.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private ApiService apiService;
    private SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etLogUsername);
        etPassword = findViewById(R.id.etLogPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        apiService = DbClient.getClient().create(ApiService.class);
        prefManager = new SharedPrefManager(this);

        btnLogin.setOnClickListener(v -> prosesLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void prosesLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan Password wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Mengecek Data...");

        apiService.login(username, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Masuk");

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse res = response.body();

                    if (res.getStatus().equals("success")) {
                        // Simpan sesi login agar tidak perlu login lagi nanti
                        prefManager.saveUser(res.getUserId(), res.getUsername());

                        Toast.makeText(LoginActivity.this, "Selamat datang, " + res.getUsername() + "!", Toast.LENGTH_SHORT).show();

                        // Lempar ke MainActivity (Abaikan jika merah, nanti kita buat)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Tutup halaman login
                    } else {
                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Masuk");
                Toast.makeText(LoginActivity.this, "Koneksi ke Server Gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}