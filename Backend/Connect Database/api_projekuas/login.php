<?php
require 'koneksi.php';

// Menangkap data dari Android menggunakan $_POST (Format Form)
$username = $_POST['username'] ?? null;
$password_input = $_POST['password'] ?? null;

if($username && $password_input){
    // Mencari user berdasarkan username
    $query = "SELECT * FROM users WHERE username='$username'";
    $result = $conn->query($query);
    
    if($result->num_rows > 0){
        $user = $result->fetch_assoc();
        
        // Mencocokkan password
        if(password_verify($password_input, $user['password'])){
            echo json_encode([
                "status" => "success", 
                "message" => "Login berhasil!",
                "user_id" => $user['id'],
                "username" => $user['username']
            ]);
        } else {
            echo json_encode(["status" => "error", "message" => "Password salah!"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Username tidak ditemukan!"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Data tidak lengkap."]);
}
?>