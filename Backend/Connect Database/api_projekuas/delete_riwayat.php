<?php
require 'koneksi.php';

$id = $_POST['id'] ?? '';

if($id != ''){
    $query = "DELETE FROM history_prediksi WHERE id='$id'";
    if($conn->query($query) === TRUE){
        echo json_encode(["status" => "success", "message" => "Riwayat berhasil dihapus!"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Gagal menghapus data."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "ID Riwayat tidak valid."]);
}
?>