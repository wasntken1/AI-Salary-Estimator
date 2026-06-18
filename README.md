# 🤖 AI Salary Estimator

AI Salary Estimator adalah aplikasi *mobile* berbasis Android yang dirancang untuk membantu lulusan baru (*fresh graduates*) dalam menentukan estimasi gaji yang adil dan objektif. Aplikasi ini mengintegrasikan antarmuka Android native dengan model Machine Learning (Linear Regression) di sisi *backend* untuk memberikan prediksi gaji secara *real-time* berdasarkan kualifikasi akademik dan teknis pengguna.

---

## 👥 Anggota Kelompok
Proyek ini dikembangkan untuk memenuhi Ujian Akhir Semester (UAS) mata kuliah Pemrograman Mobile.
* **Dayang Amanina Nalingga (2432071)**
* **Djanu Ariya Mulya (2432077)**
* **Kevin Dhiyo Fazilki (2432060)**
* **Muhamad Hisyam Fata (2432082)**
* **Rasya Zidan Putra Yuandy Soeseno (2432010)**

---

## 📱 Fitur Utama
Aplikasi ini telah memenuhi seluruh kriteria wajib pengembangan, meliputi:
1. **Autentikasi Aman:** Fitur Login dan Registrasi yang terintegrasi dengan API dan MySQL.
2. **Sistem Pakar AI:** Prediksi gaji instan menggunakan model *Machine Learning* via Python Flask.
3. **Manajemen Riwayat (CRUD):** Kemampuan untuk Membuat (Simpan), Membaca (History), Memperbarui (Update), dan Menghapus (Delete) riwayat estimasi.
4. **Optimasi Antarmuka:** Penggunaan `RecyclerView` dengan `ViewHolder` untuk performa *scrolling* riwayat yang mulus.
5. **Navigasi Dinamis:** Implementasi `BottomNavigationView` dan arsitektur *Fragment*.
6. **Pencarian Real-Time:** Fitur *Search* untuk memfilter data riwayat secara instan.
7. **Manajemen Sesi:** Implementasi `SharedPreferences` untuk menyimpan sesi pengguna dan fitur *Logout*.

---

## 🛠️ Teknologi & Tools
* **Frontend:** Java (Android Studio)
* **Backend API (CRUD):** PHP Native
* **Backend API (Machine Learning):** Python (Flask Framework)
* **Machine Learning:** Scikit-Learn (`joblib`, Linear Regression)
* **Database:** MySQL (via XAMPP / phpMyAdmin)
* **Network & Parsing:** Retrofit & GSON

---

## 🚀 Cara Instalasi & Menjalankan Aplikasi (How to Run)

Bagi penguji atau pengguna yang ingin menjalankan aplikasi ini secara lokal, ikuti langkah-langkah berikut:

### 1. Persiapan Database
1. Nyalakan modul **Apache** dan **MySQL** pada XAMPP.
2. Buka `http://localhost/phpmyadmin`.
3. Buat database baru bernama `db_projekuas`.
4. jalankan query 
```sql
USE db_projekuas;

-- Membuat Tabel Akun Pengguna (Untuk Syarat Login & Register)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Membuat Tabel Riwayat Prediksi AI (Untuk Syarat CRUD & RecyclerView)
CREATE TABLE history_prediksi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ipk DECIMAL(3,2) NOT NULL,           
    tier_kampus INT NOT NULL,
    skor_koding DECIMAL(5,2) NOT NULL,   
    tipe_perusahaan VARCHAR(50) NOT NULL,
    posisi VARCHAR(50) NOT NULL,
    hasil_gaji VARCHAR(100) NOT NULL,    
    catatan TEXT,                        
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Menghubungkan riwayat dengan pemilik akun (Relasi Database)
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```
### 2. Konfigurasi Backend (PHP & Flask)
1. Pindahkan folder `Connect Database` di Folder Backend ke dalam direktori `C:\xampp\htdocs\`.
2. Buka *terminal* atau *command prompt*, arahkan ke folder `Rest API` di folder Backed.
3. Instal pustaka Python yang dibutuhkan dengan menjalankan perintah: 
   `pip install flask pandas scikit-learn joblib`
4. Jalankan server Machine Learning dengan perintah: 
   `python app.py` (Server akan berjalan di port 5000).

### 4. Konfigurasi Frontend (Web)
1. Buka Browser di device yang sama dengan python app.py
2. Masuk ke URL yang di reply Flask di vscode

### 5. Konfigurasi Frontend (Android Studio)
1. Buka folder `projekuas` di Folder Android Studio menggunakan Android Studio.
2. Buka file `DbClient.java` dan `ApiClient.java`.
3. Ubah **Base URL** dengan *IP Address* lokal komputer Anda (contoh: `http://192.168.1.5/backend_php/` dan `http://192.168.1.5:5000/`).
4. Lakukan *Sync Project with Gradle Files*.
5. Jalankan aplikasi pada Emulator atau Perangkat Fisik (pastikan terhubung di jaringan WiFi yang sama dengan komputer server).

Link Presentasi Projek :
to be added!
