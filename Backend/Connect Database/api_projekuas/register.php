<?php
require 'koneksi.php';

// Menangkap data dari Android menggunakan $_POST (Format Form)
$username = $_POST['username'] ?? null;
$password_input = $_POST['password'] ?? null;

if($username && $password_input){
    // Mengamankan password
    $password = password_hash($password_input, PASSWORD_DEFAULT); 
    
    // Mengecek apakah username sudah pernah dipakai
    $cek_user = $conn->query("SELECT * FROM users WHERE username='$username'");
    if($cek_user->num_rows > 0){
        echo json_encode(["status" => "error", "message" => "Username sudah terdaftar!"]);
    } else {
        // Menyimpan user baru
        $query = "INSERT INTO users (username, password) VALUES ('$username', '$password')";
        if($conn->query($query) === TRUE){
            echo json_encode(["status" => "success", "message" => "Registrasi berhasil!"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Gagal menyimpan data."]);
        }
    }
} else {
    echo json_encode(["status" => "error", "message" => "Data tidak lengkap."]);
}
?>