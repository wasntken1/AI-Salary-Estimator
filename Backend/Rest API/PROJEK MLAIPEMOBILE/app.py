from flask import Flask, request, render_template, jsonify
import joblib
import pandas as pd

app = Flask(__name__)

# 1. Memuat model final dan susunan kolom hasil encoding dari Colab
try:
    model = joblib.load('model_gaji_final.pkl')
    model_columns = joblib.load('kolom_fitur_gaji_final.pkl')
except Exception as e:
    print(f"Error saat memuat file .pkl: {e}")
    print("Pastikan file 'model_gaji_final.pkl' dan 'kolom_fitur_gaji_final.pkl' ada di folder yang sama.")

def hitung_estimasi_gaji(cgpa_input, college_tier, coding_score, company_type, job_role):
    """
    Fungsi internal untuk memproses data input menggunakan model ML final
    dan mengonversinya ke dalam skala gaji Rupiah lokal.
    """
    # ---------------------------------------------------------
    # KONVERSI IPK (CGPA) DARI SKALA 4.0 (Lokal) KE 10.0 (Global)
    # ---------------------------------------------------------
    cgpa_model = (cgpa_input / 4.0) * 10.0

    # Mengemas data ke bentuk DataFrame SESUAI DENGAN FITUR BARU
    data_input = {
        'college_tier': [college_tier],
        'cgpa': [cgpa_model],
        'coding_score': [coding_score],
        'company_type': [company_type],
        'job_role': [job_role]
    }
    df_input = pd.DataFrame(data_input)
    
    # One-Hot Encoding otomatis untuk kolom teks ('company_type' dan 'job_role')
    df_encoded = pd.get_dummies(df_input)
    
    # Menyelaraskan kolom dengan standar training model di Colab
    df_final = df_encoded.reindex(columns=model_columns, fill_value=0)
    
    # Prediksi nilai target asli (satuan LPA - Lakh Per Annum)
    prediksi_lpa = model.predict(df_final)[0]
    
    # Rumus Konversi Finansial ke Rupiah (1 Lakh = 100.000 INR | 1 INR = 183.16 IDR)
    gaji_inr_tahunan = prediksi_lpa * 100000
    gaji_idr_tahunan_global = gaji_inr_tahunan * 183.16
    
    # PENYESUAIAN REALITA LOKAL (Localization Index = 10%)
    indeks_lokal = 0.1
    gaji_idr_tahunan_lokal = gaji_idr_tahunan_global * indeks_lokal
    gaji_idr_bulanan_lokal = gaji_idr_tahunan_lokal / 12
    
    # Merapikan format angka menjadi mata uang Rupiah standar
    tampilan_bulan = f"Rp {gaji_idr_bulanan_lokal:,.2f}".replace(",", ".")
    tampilan_tahun = f"Rp {gaji_idr_tahunan_lokal:,.2f}".replace(",", ".")
    
    # Memperbaiki tanda sen koma di bagian paling belakang (misal dari .00 menjadi ,00)
    tampilan_bulan = tampilan_bulan[:-3] + "," + tampilan_bulan[-2:]
    tampilan_tahun = tampilan_tahun[:-3] + "," + tampilan_tahun[-2:]
    
    return tampilan_bulan, tampilan_tahun


# ==================================================
# JALUR 1: RENDER HALAMAN WEB (UNTUK BROWSER)
# ==================================================
@app.route('/', methods=['GET', 'POST'])
def home():
    tampilan_bulan = None
    tampilan_tahun = None
    
    if request.method == 'POST':
        try:
            # Menangkap data dari form input HTML berdasarkan FITUR BARU
            cgpa = float(request.form['cgpa'])
            college_tier = int(request.form['college_tier'])
            coding_score = float(request.form['coding_score'])
            company_type = request.form['company_type']
            job_role = request.form['job_role']
            
            # Memproses prediksi
            tampilan_bulan, tampilan_tahun = hitung_estimasi_gaji(
                cgpa, college_tier, coding_score, company_type, job_role
            )
        except Exception as e:
            tampilan_bulan = f"Error pemrosesan: {str(e)}"
            
    return render_template('index.html', hasil_bulan=tampilan_bulan, hasil_tahun=tampilan_tahun)


# ==================================================
# JALUR 2: API KHUSUS JSON (UNTUK ANDROID STUDIO / JAVA)
# ==================================================
@app.route('/api/predict', methods=['POST'])
def predict_android():
    try:
        # Menangkap data berformat JSON yang dilempar dari Android
        data = request.json
        if not data:
            return jsonify({'status': 'error', 'message': 'Data JSON tidak ditemukan'}), 400
            
        # Ekstraksi parameter dari request Android berdasarkan FITUR BARU
        cgpa = float(data.get('cgpa', 0))
        college_tier = int(data.get('college_tier', 1)) # Default tier 1
        coding_score = float(data.get('coding_score', 0))
        company_type = data.get('company_type', 'MNC')
        job_role = data.get('job_role', 'Software Engineer')
        
        # Memproses prediksi
        tampilan_bulan, _ = hitung_estimasi_gaji(
            cgpa, college_tier, coding_score, company_type, job_role
        )
        
        # Mengembalikan balasan murni teks JSON ke HP Android
        return jsonify({
            'status': 'success',
            'estimasi_gaji_bulan': tampilan_bulan
        })
        
    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': f"Gagal memproses data di server: {str(e)}"
        }), 500


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)