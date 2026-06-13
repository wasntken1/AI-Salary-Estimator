package com.example.projekuas.model;

import com.google.gson.annotations.SerializedName;

public class HistoryItem {
    @SerializedName("id")
    private int id;

    @SerializedName("posisi")
    private String posisi;

    @SerializedName("tipe_perusahaan")
    private String tipePerusahaan;

    @SerializedName("hasil_gaji")
    private String hasilGaji;

    @SerializedName("catatan")
    private String catatan;

    // Getter untuk mengambil nilai
    public int getId() { return id; }
    public String getPosisi() { return posisi; }
    public String getTipePerusahaan() { return tipePerusahaan; }
    public String getHasilGaji() { return hasilGaji; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}