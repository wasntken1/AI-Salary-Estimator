<?php
require 'koneksi.php';

// Menangkap ID User dari Android (Menggunakan GET)
$user_id = $_GET['user_id'] ?? '';

if($user_id != ''){
    // Mengambil data riwayat dari yang paling baru (DESC)
    $query = "SELECT * FROM history_prediksi WHERE user_id='$user_id' ORDER BY created_at DESC";
    $result = $conn->query($query);
    
    $data = [];
    if($result->num_rows > 0){
        while($row = $result->fetch_assoc()){
            $data[] = $row;
        }
        echo json_encode(["status" => "success", "data" => $data]);
    } else {
        echo json_encode(["status" => "empty", "message" => "Belum ada riwayat prediksi."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "ID User tidak ditemukan."]);
}
?>