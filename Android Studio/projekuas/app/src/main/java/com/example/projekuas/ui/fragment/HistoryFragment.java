package com.example.projekuas.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekuas.R;
import com.example.projekuas.adapter.HistoryAdapter;
import com.example.projekuas.api.ApiService;
import com.example.projekuas.api.DbClient;
import com.example.projekuas.model.HistoryItem;
import com.example.projekuas.model.HistoryResponse;
import com.example.projekuas.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private EditText etSearchHistory;

    private HistoryAdapter adapter;
    private List<HistoryItem> historyListAsli; // Menyimpan data utuh

    private ApiService dbService;
    private SharedPrefManager prefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        tvEmptyHistory = view.findViewById(R.id.tvEmptyHistory);
        etSearchHistory = view.findViewById(R.id.etSearchHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyListAsli = new ArrayList<>();

        prefManager = new SharedPrefManager(requireContext());
        dbService = DbClient.getClient().create(ApiService.class);

        tarikDataDariMySQL();

        // Memantau setiap ketikan di kolom pencarian
        etSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                jalankanFilterPencarian(s.toString());
            }
        });

        return view;
    }

    private void tarikDataDariMySQL() {
        int userId = prefManager.getUserId();

        dbService.getRiwayat(userId).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistoryResponse res = response.body();

                    if ("success".equals(res.getStatus())) {
                        historyListAsli = res.getData(); // Simpan data utuh

                        // Masukkan ke adapter
                        adapter = new HistoryAdapter(requireContext(), historyListAsli);
                        rvHistory.setAdapter(adapter);

                        rvHistory.setVisibility(View.VISIBLE);
                        tvEmptyHistory.setVisibility(View.GONE);
                        etSearchHistory.setVisibility(View.VISIBLE);
                    } else {
                        rvHistory.setVisibility(View.GONE);
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                        etSearchHistory.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal menarik data server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void jalankanFilterPencarian(String teksPencarian) {
        List<HistoryItem> dataTersaring = new ArrayList<>();

        for (HistoryItem item : historyListAsli) {
            // Mencocokkan teks dengan Posisi atau Perusahaan (mengabaikan huruf besar/kecil)
            if (item.getPosisi().toLowerCase().contains(teksPencarian.toLowerCase()) ||
                    item.getTipePerusahaan().toLowerCase().contains(teksPencarian.toLowerCase())) {
                dataTersaring.add(item);
            }
        }

        // Kirim data yang sudah disaring ke Adapter
        if (adapter != null) {
            adapter.setFilteredList(dataTersaring);
        }
    }
}