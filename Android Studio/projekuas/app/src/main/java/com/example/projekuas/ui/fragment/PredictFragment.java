package com.example.projekuas.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projekuas.R;
import com.example.projekuas.api.ApiClient;
import com.example.projekuas.api.ApiService;
import com.example.projekuas.api.DbClient;
import com.example.projekuas.model.PredictionRequest;
import com.example.projekuas.model.PredictionResponse;
import com.example.projekuas.model.SimpleResponse;
import com.example.projekuas.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictFragment extends Fragment {

    private EditText etCgpa, etCoding;
    private Spinner spinnerTier, spinnerCompany, spinnerRole;
    private Button btnPredict;
    private TextView tvResult;

    // Variabel untuk menampung layanan API
    private ApiService aiService;
    private ApiService dbService;
    private SharedPrefManager prefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menghubungkan fragment dengan layout XML
        View view = inflater.inflate(R.layout.fragment_predict, container, false);

        // 1. Inisialisasi Komponen UI
        etCgpa = view.findViewById(R.id.etCgpa);
        etCoding = view.findViewById(R.id.etCoding);
        spinnerTier = view.findViewById(R.id.spinnerTier);
        spinnerCompany = view.findViewById(R.id.spinnerCompany);
        spinnerRole = view.findViewById(R.id.spinnerRole);
        btnPredict = view.findViewById(R.id.btnPredict);
        tvResult = view.findViewById(R.id.tvResult);

        // 2. Inisialisasi Utility & API Client
        prefManager = new SharedPrefManager(requireContext());

        // aiService mengarah ke Flask (Port 5000)
        aiService = ApiClient.getClient().create(ApiService.class);

        // dbService mengarah ke XAMPP PHP (Port 80)
        dbService = DbClient.getClient().create(ApiService.class);

        // 3. Memasang data pilihan ke Dropdown (Spinner)
        setupSpinnerData();

        // 4. Konfigurasi Aksi Klik Tombol
        btnPredict.setOnClickListener(v -> jalankanProsesPrediksiAI());

        return view;
    }

    private void setupSpinnerData() {
        // Data untuk Level Kampus
        String[] tiers = {
                "Tier 1 (Kampus Top Elite / Nasional)",
                "Tier 2 (Kampus Menengah / Regional)",
                "Tier 3 (Kampus Berkembang)"
        };
        ArrayAdapter<String> tierAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiers);
        spinnerTier.setAdapter(tierAdapter);

        // Data untuk Tipe Perusahaan
        String[] companies = {"MNC", "Top Tech", "Mid-size", "Startup"};
        ArrayAdapter<String> compAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, companies);
        spinnerCompany.setAdapter(compAdapter);

        // Data untuk Posisi Pekerjaan
        String[] roles = {"Software Engineer", "Data Scientist", "Web Developer", "Business Analyst"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerRole.setAdapter(roleAdapter);
    }

    private void jalankanProsesPrediksiAI() {
        String stringCgpa = etCgpa.getText().toString().trim();
        String stringCoding = etCoding.getText().toString().trim();

        // Validasi agar input tidak kosong
        if (stringCgpa.isEmpty() || stringCoding.isEmpty()) {
            Toast.makeText(requireContext(), "Harap isi semua kolom input angka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mengubah input menjadi tipe data numerik
        double cgpaInput = Double.parseDouble(stringCgpa);
        // Mengambil urutan posisi yang dipilih (dimulai dari 0) lalu ditambah 1.
        // Jika memilih posisi 0 (Tier 1), maka hasilnya 0 + 1 = 1.
        // Jika memilih posisi 1 (Tier 2), maka hasilnya 1 + 1 = 2.
        int posisiDipilih = spinnerTier.getSelectedItemPosition();
        int tierInput = posisiDipilih + 1;
        double codingInput = Double.parseDouble(stringCoding);
        String companyInput = spinnerCompany.getSelectedItem().toString();
        String roleInput = spinnerRole.getSelectedItem().toString();

        // Validasi batasan nilai IPK lokal
        if (cgpaInput > 4.0) {
            Toast.makeText(requireContext(), "IPK maksimal adalah 4.00!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mengunci tombol agar tidak ditekan berulang kali saat proses menghitung
        btnPredict.setEnabled(false);
        tvResult.setText("Menghitung dengan kecerdasan AI...");

        // Membungkus data ke objek request
        PredictionRequest requestData = new PredictionRequest(cgpaInput, tierInput, codingInput, companyInput, roleInput);

        // TAHAP 1: Kirim data ke Python Flask (Proses Prediksi Gaji)
        aiService.getPrediction(requestData).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse aiResponse = response.body();

                    if ("success".equals(aiResponse.getStatus())) {
                        String hasilGajiMataUang = aiResponse.getEstimasiGajiBulan();

                        // Tampilkan hasil kalkulasi ke layar HP
                        tvResult.setText("Estimasi Gaji:\n" + hasilGajiMataUang);

                        // TAHAP 2: Lempar hasil tersebut ke MySQL via PHP XAMPP (Otomatis Simpan Riwayat)
                        simpanHasilKeDatabaseMySQL(cgpaInput, tierInput, codingInput, companyInput, roleInput, hasilGajiMataUang);

                    } else {
                        btnPredict.setEnabled(true);
                        tvResult.setText("Gagal: " + aiResponse.getMessage());
                    }
                } else {
                    btnPredict.setEnabled(true);
                    tvResult.setText("Error: Respon server AI tidak valid.");
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                btnPredict.setEnabled(true);
                tvResult.setText("Koneksi gagal! Pastikan Server Flask Aktif.");
                Toast.makeText(requireContext(), "Gagal terhubung ke Flask: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void simpanHasilKeDatabaseMySQL(double ipk, int tier, double coding, String company, String role, String gaji) {
        // Mengambil ID User dari sesi aktif Shared Preferences
        int currentUserId = prefManager.getUserId();

        // Mengirim data ke simpan_riwayat.php menggunakan dbService (DbClient)
        dbService.simpanRiwayat(currentUserId, ipk, tier, coding, company, role, gaji).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                // Membuka kembali kunci tombol setelah seluruh rangkaian proses selesai
                btnPredict.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    SimpleResponse dbResponse = response.body();
                    if ("success".equals(dbResponse.getStatus())) {
                        Toast.makeText(requireContext(), "Sukses! Tersimpan otomatis ke Riwayat.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Gagal menyimpan: " + dbResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                btnPredict.setEnabled(true);
                Toast.makeText(requireContext(), "Gagal sinkronisasi data riwayat ke MySQL lokal.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}