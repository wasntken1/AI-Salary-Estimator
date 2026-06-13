<?php
require 'koneksi.php';

$user_id = $_POST['user_id'] ?? '';
$ipk = $_POST['ipk'] ?? '';
$tier_kampus = $_POST['tier_kampus'] ?? '';
$skor_koding = $_POST['skor_koding'] ?? '';
$tipe_perusahaan = $_POST['tipe_perusahaan'] ?? '';
$posisi = $_POST['posisi'] ?? '';
$hasil_gaji = $_POST['hasil_gaji'] ?? '';

// 1. Cek apakah ID User dari sesi Android terbaca
if ($user_id == '') {
    echo json_encode(["status" => "error", "message" => "ID User kosong! Silakan Logout dan Login kembali."]);
    exit;
}

// 2. Cek apakah IPK masuk
if ($ipk == '') {
    echo json_encode(["status" => "error", "message" => "Data IPK tidak diterima oleh server."]);
    exit;
}

// 3. Proses Simpan
$query = "INSERT INTO history_prediksi (user_id, ipk, tier_kampus, skor_koding, tipe_perusahaan, posisi, hasil_gaji, catatan) 
          VALUES ('$user_id', '$ipk', '$tier_kampus', '$skor_koding', '$tipe_perusahaan', '$posisi', '$hasil_gaji', '')";

if ($conn->query($query) === TRUE) {
    echo json_encode(["status" => "success", "message" => "Riwayat berhasil disimpan!"]);
} else {
    // Akan menampilkan error database secara spesifik (misal: kolom tidak cocok)
    echo json_encode(["status" => "error", "message" => "Error MySQL: " . $conn->error]);
}
?>