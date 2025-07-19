package com.RealizeStudio.qritik.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel

class ScannerResultScreenViewModel: ViewModel() {

    // Yardımcı fonksiyonlar
     fun copyToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR Code", text)
        clipboardManager.setPrimaryClip(clip)
    }

     fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Paylaş"))
    }

     fun isUrl(text: String): Boolean {
        return text.startsWith("http://") || text.startsWith("https://")
    }

     fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "URL açılamadı", Toast.LENGTH_SHORT).show()
        }
    }

     fun isWifiQR(text: String): Boolean {
        return true // Geçici test için - her zaman true döndür
    }

     fun connectToWifi(context: Context, wifiData: String) {
        try {
            // DEBUG: Fonksiyonun çağrıldığını kontrol et
            android.util.Log.d("WiFiDebug", "connectToWifi çağrıldı: '$wifiData'")
            Toast.makeText(context, "WiFi bağlan butonuna basıldı!", Toast.LENGTH_SHORT).show()

            if (wifiData.startsWith("WIFI")) {
                // Standart WiFi QR format
                android.util.Log.d("WiFiDebug", "Standart WiFi formatı tespit edildi")
                val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
                Toast.makeText(context, "WiFi ayarlarına yönlendiriliyorsunuz", Toast.LENGTH_SHORT).show()
            } else {
                // Özel format: "SSID Password"
                android.util.Log.d("WiFiDebug", "Özel WiFi formatı işleniyor")
                val parts = wifiData.trim().split(" ")
                android.util.Log.d("WiFiDebug", "Bölüm sayısı: ${parts.size}, Bölümler: $parts")

                if (parts.size == 2) {
                    val ssid = parts[0]
                    val password = parts[1]

                    android.util.Log.d("WiFiDebug", "SSID: '$ssid', Password: '$password'")

                    // WiFi bilgilerini göster
                    Toast.makeText(context,
                        "WiFi: $ssid\nŞifre: $password\nWiFi ayarlarına yönlendiriliyorsunuz",
                        Toast.LENGTH_LONG).show()

                    // WiFi ayarlarını aç
                    val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                    context.startActivity(intent)
                } else {
                    android.util.Log.d("WiFiDebug", "Bölüm sayısı uygun değil: ${parts.size}")
                    Toast.makeText(context, "WiFi formatı tanınamadı: ${parts.size} bölüm", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WiFiDebug", "Hata: ${e.message}")
            Toast.makeText(context, "WiFi ayarları açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}