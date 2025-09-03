package com.RealizeStudio.qritik.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScannerResultScreenViewModel @Inject constructor(): ViewModel() {

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
        return text.startsWith("WIFI:") &&
                text.startsWith("WIFI:") &&
                text.contains("S:") &&
                (text.contains("T:WPA") || text.contains("T:WEP") || text.contains("T:nopass")) &&
                text.contains("P:") &&
                text.endsWith(";;")
    }


    fun connectToWifi(context: Context, wifiData: String) {
        //Wifi ototmotik bağlanılmıyor sebebi android 10 ve sonrası sürümlerde kullanıcıdann zizin gereketirmesi.
        //Bu yöntem ile kullanıcı wifi bağlanma ekrnaına yönlenidirliyor ve kullanıcıya şifreyi kendisi girmesi için izin veriliyor.
        try {
            android.util.Log.d("WiFiDebug", "connectToWifi çağrıldı: '$wifiData'")
            Toast.makeText(context, "WiFi bağlan butonuna basıldı!", Toast.LENGTH_SHORT).show()

            val trimmedData = wifiData.trim()
            android.util.Log.d("WiFiDebug", "trimmedData: '$trimmedData'")

            val parts = trimmedData.split("\\s+".toRegex())
            android.util.Log.d("WiFiDebug", "parts.size: ${parts.size}, parts: $parts")

            if (parts.size >= 2) {
                val password = parts.last()
                val ssid = parts.subList(0, parts.size - 1).joinToString(" ")

                android.util.Log.d("WiFiDebug", "SSID: '$ssid', Şifre: '$password'")

                Toast.makeText(
                    context,
                    "Ağ: $ssid\nŞifre: $password\nLütfen manuel bağlanın.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else if (trimmedData.startsWith("WIFI:")) {
                android.util.Log.d("WiFiDebug", "WIFI: formatı tespit edildi.")
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                Toast.makeText(context, "WiFi ayarlarına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show()
            } else {
                android.util.Log.d("WiFiDebug", "Tanınmayan WiFi formatı.")
                Toast.makeText(context, "Tanınmayan WiFi formatı:\n$wifiData", Toast.LENGTH_LONG).show()

                val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(fallbackIntent)
            }

        } catch (e: Exception) {
            android.util.Log.e("WiFiDebug", "Hata: ${e.message}")
            Toast.makeText(context, "WiFi ayarları açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }





    //Yardımcı fonksiyon
    fun generateQrCode(content: String, size: Int = 512): Bitmap {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }

        return bitmap
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun generateBarcode(content: String, format: BarcodeFormat, width: Int, height: Int): Bitmap {
        val bitMatrix = MultiFormatWriter().encode(content, format, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)

            }
        }
        return bitmap
    }

    fun openDialerWithNumber(context: Context, phoneNumber: String){

        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Toast.makeText(context,"Telefon ekranına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(context,"Hata oluştu.", Toast.LENGTH_SHORT).show()
        }
    }




}