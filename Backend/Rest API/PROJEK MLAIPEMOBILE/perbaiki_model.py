import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import joblib

print("1. Membaca dataset...")
df = pd.read_csv('student_placement_salary_elite_v2.csv')

# Fokus pada mahasiswa yang sudah bekerja
df_bekerja = df[df['placed'] == 1].copy()

# Mengambil fitur yang BENAR sesuai desain web terbaru
cols = ['salary_lpa', 'job_role', 'company_type', 'college_tier', 'cgpa', 'coding_score']
df_pilihan = df_bekerja[cols].dropna()

Y = df_pilihan['salary_lpa']
X_raw = df_pilihan.drop(columns=['salary_lpa'])

print("2. Melakukan One-Hot Encoding...")
X = pd.get_dummies(X_raw, drop_first=True)

X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=0.2, random_state=42)

print("3. Melatih Model AI...")
lr_model = LinearRegression()
lr_model.fit(X_train, Y_train)

# Mengekspor (Menimpa) file .pkl lama dengan yang baru dan 100% cocok
joblib.dump(lr_model, 'model_gaji_final.pkl')
joblib.dump(list(X.columns), 'kolom_fitur_gaji_final.pkl')

print("\nSUKSES! File .pkl berhasil diperbaiki.")
print("Susunan Kolom yang tersimpan sekarang:")
print(list(X.columns))