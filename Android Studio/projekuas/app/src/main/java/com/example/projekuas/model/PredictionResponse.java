package com.example.projekuas.model;

import com.google.gson.annotations.SerializedName;

public class PredictionResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("estimasi_gaji_bulan")
    private String estimasiGajiBulan;

    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getEstimasiGajiBulan() { return estimasiGajiBulan; }
    public String getMessage() { return message; }
}