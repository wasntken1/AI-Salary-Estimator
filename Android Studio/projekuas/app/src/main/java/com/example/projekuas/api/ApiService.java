package com.example.projekuas.api;

import com.example.projekuas.model.PredictionRequest;
import com.example.projekuas.model.PredictionResponse;
import com.example.projekuas.model.AuthResponse;
import com.example.projekuas.model.SimpleResponse;
import com.example.projekuas.model.HistoryResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    // ==========================================
    // 1. JALUR KE FLASK (AI MACHINE LEARNING)
    // ==========================================
    @POST("api/predict")
    Call<PredictionResponse> getPrediction(@Body PredictionRequest request);

    // ==========================================
    // 2. JALUR KE XAMPP (DATABASE MYSQL)
    // ==========================================

    @FormUrlEncoded
    @POST("register.php")
    Call<AuthResponse> register(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<AuthResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("simpan_riwayat.php")
    Call<SimpleResponse> simpanRiwayat(
            @Field("user_id") int userId,
            @Field("ipk") double ipk,
            @Field("tier_kampus") int tierKampus,
            @Field("skor_koding") double skorKoding,
            @Field("tipe_perusahaan") String tipePerusahaan,
            @Field("posisi") String posisi,
            @Field("hasil_gaji") String hasilGaji
    );
    @GET("get_riwayat.php")
    Call<HistoryResponse> getRiwayat(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("update_riwayat.php")
    Call<SimpleResponse> updateRiwayat(
            @Field("id") int id,
            @Field("catatan") String catatan
    );

    @FormUrlEncoded
    @POST("delete_riwayat.php")
    Call<SimpleResponse> deleteRiwayat(
            @Field("id") int id
    );
}