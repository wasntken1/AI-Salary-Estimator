<?php
require 'koneksi.php';

$id = $_POST['id'] ?? '';
$catatan = $_POST['catatan'] ?? '';

if($id != ''){
    $query = "UPDATE history_prediksi SET catatan='$catatan' WHERE id='$id'";
    if($conn->query($query) === TRUE){
        echo json_encode(["status" => "success", "message" => "Catatan berhasil disimpan!"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Gagal menyimpan catatan."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "ID Riwayat tidak valid."]);
}
?>