# BensinKu — Colorful Dashboard

Versi multi-kendaraan dengan UI biru-merah-hijau-oranye sesuai mockup.

## Fitur
- Dashboard BBM mobile-first
- Multi kendaraan + nama panggilan
- Upload foto kendaraan
- Riwayat pengisian per kendaraan
- Hitung liter otomatis dari total bayar / harga per liter
- Efisiensi km/L, biaya/km, total bulanan
- Statistik sederhana
- Harga BBM per kendaraan + tombol refresh status
- Export/import JSON
- File chooser Android untuk foto & import JSON
- Export JSON ke folder Downloads pada Android

## Build APK di GitHub
1. Upload seluruh isi folder ini ke repository GitHub.
2. Buka **Actions > Build APK > Run workflow**.
3. Setelah sukses, buka job dan download artifact **BensinKu-debug-apk**.

Catatan: tombol refresh harga sudah memiliki UI/loading dan menyimpan waktu pengecekan. Harga otomatis dari internet belum diikat ke API resmi karena endpoint harga BBM publik yang stabil belum tersedia di project ini; harga tetap dapat diperbarui manual.
