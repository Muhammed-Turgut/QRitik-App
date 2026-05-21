package com.RealizeStudio.qritik.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.RealizeStudio.qritik.R

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
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }

     fun isUrl(text: String): Boolean {
        return text.startsWith("http://") || text.startsWith("https://")
    }

     fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.url_cannot_be_opened), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, context.getString(R.string.wifi_connect_clicked), Toast.LENGTH_SHORT).show()

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
                    context.getString(R.string.wifi_connect_manual, ssid, password),
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
                Toast.makeText(context, context.getString(R.string.wifi_redirecting), Toast.LENGTH_SHORT).show()
            } else {
                android.util.Log.d("WiFiDebug", "Tanınmayan WiFi formatı.")
                Toast.makeText(context, context.getString(R.string.wifi_unknown_format, wifiData), Toast.LENGTH_LONG).show()

                val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(fallbackIntent)
            }

        } catch (e: Exception) {
            android.util.Log.e("WiFiDebug", "Hata: ${e.message}")
            Toast.makeText(context, context.getString(R.string.wifi_settings_failed, e.message ?: ""), Toast.LENGTH_SHORT).show()
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

    fun generateStyledQrCode(
        content: String,
        size: Int = 512,
        foregroundColor: Int = android.graphics.Color.BLACK,
        backgroundColor: Int = android.graphics.Color.WHITE,
        dotShape: String = "SQUARE", // SQUARE, CIRCLE, ROUNDED
        eyeShape: String = "SQUARE", // SQUARE, ROUNDED, CIRCLE
        gradientStartColor: Int? = null,
        gradientEndColor: Int? = null,
        logoBitmap: Bitmap? = null
    ): Bitmap {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )
            val M = bitMatrix.width
            val moduleSize = size.toFloat() / M

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Draw background
            val bgPaint = Paint().apply {
                color = backgroundColor
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)

            // Prepare Foreground paint
            val fgPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            if (gradientStartColor != null && gradientEndColor != null) {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    gradientStartColor, gradientEndColor,
                    Shader.TileMode.CLAMP
                )
            } else {
                fgPaint.color = foregroundColor
            }

            // Loop and draw modules
            for (i in 0 until M) {
                for (j in 0 until M) {
                    // Check if it belongs to any of the three Finder Patterns (Eyes)
                    val isEye = (i < 7 && j < 7) || // Top-Left
                                (i >= M - 7 && j < 7) || // Top-Right
                                (i < 7 && j >= M - 7)   // Bottom-Left

                    if (isEye) {
                        // Skip individual eye pixels, we will draw the three eyes as a whole beautifully
                        continue
                    }

                    if (bitMatrix.get(i, j)) {
                        val left = i * moduleSize
                        val top = j * moduleSize
                        val right = left + moduleSize
                        val bottom = top + moduleSize

                        when (dotShape) {
                            "CIRCLE" -> {
                                val centerX = left + moduleSize / 2f
                                val centerY = top + moduleSize / 2f
                                val radius = (moduleSize / 2f) * 0.85f
                                canvas.drawCircle(centerX, centerY, radius, fgPaint)
                            }
                            "ROUNDED" -> {
                                val rectF = RectF(
                                    left + moduleSize * 0.05f,
                                    top + moduleSize * 0.05f,
                                    right - moduleSize * 0.05f,
                                    bottom - moduleSize * 0.05f
                                )
                                canvas.drawRoundRect(rectF, moduleSize * 0.35f, moduleSize * 0.35f, fgPaint)
                            }
                            else -> { // SQUARE
                                canvas.drawRect(left, top, right, bottom, fgPaint)
                            }
                        }
                    }
                }
            }

            // Draw custom Finder Patterns (Eyes)
            drawEye(canvas, 0f, 0f, moduleSize, eyeShape, fgPaint, backgroundColor) // Top-Left
            drawEye(canvas, (M - 7) * moduleSize, 0f, moduleSize, eyeShape, fgPaint, backgroundColor) // Top-Right
            drawEye(canvas, 0f, (M - 7) * moduleSize, moduleSize, eyeShape, fgPaint, backgroundColor) // Bottom-Left

            // Overlay Logo if present
            logoBitmap?.let { logo ->
                val logoSize = (size * 0.18f).toInt()
                val logoLeft = (size - logoSize) / 2
                val logoTop = (size - logoSize) / 2

                // Draw a solid protective white circle in the center to ensure QR code scannability
                val borderPaint = Paint().apply {
                    color = backgroundColor
                    isAntiAlias = true
                    style = Paint.Style.FILL
                }
                val margin = moduleSize * 0.5f
                canvas.drawRoundRect(
                    RectF(
                        logoLeft.toFloat() - margin,
                        logoTop.toFloat() - margin,
                        logoLeft.toFloat() + logoSize + margin,
                        logoTop.toFloat() + logoSize + margin
                    ),
                    moduleSize * 0.5f,
                    moduleSize * 0.5f,
                    borderPaint
                )

                // Draw the scaled logo in center
                val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
                canvas.drawBitmap(scaledLogo, logoLeft.toFloat(), logoTop.toFloat(), null)
            }

            bitmap
        } catch (e: Exception) {
            android.util.Log.e("QRDebug", "generateStyledQrCode error: ${e.message}")
            generateQrCode(content, size) // fallback to standard if anything fails
        }
    }

    private fun drawEye(
        canvas: Canvas,
        left: Float,
        top: Float,
        moduleSize: Float,
        eyeShape: String,
        fgPaint: Paint,
        bgColor: Int
    ) {
        val size = 7 * moduleSize
        val right = left + size
        val bottom = top + size

        val bgPaint = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        when (eyeShape) {
            "CIRCLE" -> {
                val cx = left + size / 2f
                val cy = top + size / 2f

                // Outer ring
                canvas.drawCircle(cx, cy, 3.5f * moduleSize, fgPaint)
                // Inner gap
                canvas.drawCircle(cx, cy, 2.5f * moduleSize, bgPaint)
                // Center core
                canvas.drawCircle(cx, cy, 1.5f * moduleSize, fgPaint)
            }
            "ROUNDED" -> {
                val outerRadius = 2.0f * moduleSize
                val innerRadius = 1.0f * moduleSize

                // Outer ring
                canvas.drawRoundRect(
                    RectF(left, top, right, bottom),
                    outerRadius, outerRadius, fgPaint
                )
                // Inner gap
                canvas.drawRoundRect(
                    RectF(left + moduleSize, top + moduleSize, right - moduleSize, bottom - moduleSize),
                    innerRadius, innerRadius, bgPaint
                )
                // Center core
                canvas.drawRoundRect(
                    RectF(left + 2f * moduleSize, top + 2f * moduleSize, right - 2f * moduleSize, bottom - 2f * moduleSize),
                    innerRadius, innerRadius, fgPaint
                )
            }
            else -> { // SQUARE
                // Outer ring
                canvas.drawRect(left, top, right, bottom, fgPaint)
                // Inner gap
                canvas.drawRect(left + moduleSize, top + moduleSize, right - moduleSize, bottom - moduleSize, bgPaint)
                // Center core
                canvas.drawRect(left + 2f * moduleSize, top + 2f * moduleSize, right - 2f * moduleSize, bottom - 2f * moduleSize, fgPaint)
            }
        }
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun generateBarcode(content: String, format: BarcodeFormat, width: Int, height: Int): Bitmap {

        return try {
            val bitMatrix = MultiFormatWriter().encode( content, format, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)

                }
            }
          bitmap
        }catch (e: Exception){
            Log.d("ScannerResultViewModel",e.message.toString())
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }


    }

    fun openDialerWithNumber(context: Context, phoneNumber: String){

        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Toast.makeText(context,context.getString(R.string.phone_redirecting), Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(context,context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_qr.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
        } catch (e: java.lang.Exception) {
            android.util.Log.e("QRDebug", "shareBitmap error: ${e.message}")
            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareImageFile(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                return
            }
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
        } catch (e: java.lang.Exception) {
            android.util.Log.e("QRDebug", "shareImageFile error: ${e.message}")
            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        try {
            val drawable = androidx.core.content.ContextCompat.getDrawable(context, drawableId) ?: return null
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: java.lang.Exception) {
            android.util.Log.e("QRDebug", "getBitmapFromVectorDrawable error: ${e.message}")
            return null
        }
    }
}