package com.example.projekuas.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projekuas.R;
import com.example.projekuas.ui.LoginActivity;
import com.example.projekuas.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {

    private ImageView ivProfile;
    private TextView tvProfileName;
    private Button btnLogout;
    private SharedPrefManager prefManager;

    // Peluncur untuk membuka Galeri HP
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mendaftarkan fungsi peluncur galeri sebelum tampilan layar dirender
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // 1. Kunci izin membaca file agar tidak hilang saat aplikasi ditutup
                            requireActivity().getContentResolver().takePersistableUriPermission(
                                    selectedImageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                            // 2. Simpan alamat file foto tersebut ke Shared Preferences
                            prefManager.saveProfileImage(selectedImageUri.toString());

                            // 3. Tampilkan fotonya menggunakan Glide (Otomatis dipotong bulat)
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(ivProfile);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        btnLogout = view.findViewById(R.id.btnLogout);

        prefManager = new SharedPrefManager(requireContext());

        // Menampilkan nama pengguna
        String username = prefManager.getUsername();
        if (username != null) {
            tvProfileName.setText(username);
        }

        // Mengecek apakah sebelumnya user sudah pernah memasang foto profil
        String savedImageUri = prefManager.getProfileImage();
        if (savedImageUri != null) {
            Glide.with(this)
                    .load(Uri.parse(savedImageUri))
                    .circleCrop()
                    .into(ivProfile);
        }

        // Aksi saat gambar profil diklik -> Buka Galeri
        ivProfile.setOnClickListener(v -> bukaGaleri());

        // Aksi Tombol Logout
        btnLogout.setOnClickListener(v -> tampilkanKonfirmasiLogout());

        return view;
    }

    private void bukaGaleri() {
        // Membuka jendela pemilihan dokumen/gambar bawaan HP
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void tampilkanKonfirmasiLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
                .setPositiveButton("Ya, Keluar", (dialog, which) -> prosesLogout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesLogout() {
        prefManager.logout();
        Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}