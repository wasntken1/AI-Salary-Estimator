package com.example.projekuas.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekuas.R;
import com.example.projekuas.api.ApiService;
import com.example.projekuas.api.DbClient;
import com.example.projekuas.model.HistoryItem;
import com.example.projekuas.model.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<HistoryItem> historyList;
    private ApiService dbService;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
        // Inisialisasi koneksi ke XAMPP
        this.dbService = DbClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        holder.tvPosisi.setText(item.getPosisi());
        holder.tvPerusahaan.setText("Tipe: " + item.getTipePerusahaan());

        // Jika catatan tidak kosong, tampilkan. Jika kosong, sembunyikan atau beri teks default.
        if (item.getCatatan() != null && !item.getCatatan().isEmpty()) {
            holder.tvGaji.setText(item.getHasilGaji() + "\n📝 Catatan: " + item.getCatatan());
        } else {
            holder.tvGaji.setText(item.getHasilGaji());
        }

        // Aksi saat kartu diklik
        holder.itemView.setOnClickListener(v -> {
            CharSequence[] options = {"📝 Edit Catatan", "🗑️ Hapus Riwayat"};

            new AlertDialog.Builder(context)
                    .setTitle("Pilih Aksi")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            tampilkanDialogEdit(item, holder.getAdapterPosition());
                        } else if (which == 1) {
                            konfirmasiHapus(item, holder.getAdapterPosition());
                        }
                    }).show();
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // ================= FUNGSI UPDATE (U) =================
    private void tampilkanDialogEdit(HistoryItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Beri Catatan Target");

        final EditText input = new EditText(context);
        input.setHint("Misal: Target tahun depan!");
        // Isi dengan catatan lama jika ada
        if (item.getCatatan() != null) {
            input.setText(item.getCatatan());
        }
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String catatanBaru = input.getText().toString().trim();
            prosesUpdateKeMySQL(item.getId(), catatanBaru, position, item);
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void prosesUpdateKeMySQL(int id, String catatanBaru, int position, HistoryItem item) {
        dbService.updateRiwayat(id, catatanBaru).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful() && "success".equals(response.body().getStatus())) {
                    Toast.makeText(context, "Catatan Disimpan!", Toast.LENGTH_SHORT).show();
                    // Update tampilan secara instan tanpa perlu reload aplikasi
                    item.setCatatan(catatanBaru); // (PERINGATAN: Akan merah sementara, baca petunjuk di bawah)
                    notifyItemChanged(position);
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Toast.makeText(context, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= FUNGSI DELETE (D) =================
    private void konfirmasiHapus(HistoryItem item, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setMessage("Yakin ingin menghapus riwayat ini?")
                .setPositiveButton("Hapus", (dialog, which) -> prosesHapusKeMySQL(item.getId(), position))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesHapusKeMySQL(int id, int position) {
        dbService.deleteRiwayat(id).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful() && "success".equals(response.body().getStatus())) {
                    Toast.makeText(context, "Riwayat dihapus!", Toast.LENGTH_SHORT).show();
                    // Hilangkan kartu dari layar secara instan
                    historyList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, historyList.size());
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Toast.makeText(context, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosisi, tvPerusahaan, tvGaji;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosisi = itemView.findViewById(R.id.tvHistPosisi);
            tvPerusahaan = itemView.findViewById(R.id.tvHistPerusahaan);
            tvGaji = itemView.findViewById(R.id.tvHistGaji);
        }
    }
    // ================= FUNGSI PENCARIAN (SEARCH) =================
    public void setFilteredList(List<HistoryItem> filteredList) {
        this.historyList = filteredList;
        notifyDataSetChanged(); // Menyegarkan tampilan dengan data hasil pencarian
    }
}