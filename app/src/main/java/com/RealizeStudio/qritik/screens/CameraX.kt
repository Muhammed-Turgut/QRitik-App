// CameraQRScanner.kt - QR kod resmi ile güncellenmiş versiyon
package com.RealizeStudio.qritik.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun CameraQRScanner(
    onQRCodeDetected: (String) -> Unit,
    onError: (String) -> Unit = {},
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var detectedCode by remember { mutableStateOf<String?>(null) }
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    var qrCodeType by remember { mutableStateOf<String?>(null) }
    var scanDateTime by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    bindCamera(
                        cameraProvider = cameraProvider,
                        previewView = previewView,
                        lifecycleOwner = lifecycleOwner,
                        context = ctx,
                        onQRCodeDetected = { code, imagePath, codeType, dateTime ->
                            if (!isProcessing) {
                                isProcessing = true
                                detectedCode = code
                                capturedImagePath = imagePath
                                qrCodeType = codeType
                                scanDateTime = dateTime
                                onQRCodeDetected(code)
                            }
                        },
                        onError = onError
                    )
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Tarama alanı göstergesi
        ScannerOverlay(
            modifier = Modifier.align(Alignment.Center)
        )

        // Sonuç gösterimi ve yönlendirme
        detectedCode?.let { code ->
            LaunchedEffect(code) {
                // QR kod verisini URL encode et
                val encodedCode = Uri.encode(code)
                // Resim yolunu URL encode et
                val encodedImagePath = Uri.encode(capturedImagePath ?: "")
                // QR kod türünü URL encode et
                val encodedCodeType = Uri.encode(qrCodeType ?: "")
                // Tarih ve saati URL encode et
                val encodedDateTime = Uri.encode(scanDateTime ?: "")
                // Veriyi navigation argument olarak aktar
                navController.navigate("ScannerResult/$encodedCode/$encodedImagePath/$encodedCodeType/$encodedDateTime")
            }
        }
    }
}

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(250.dp)
    ) {
        // Köşe çizgileri
        val cornerLength = 40.dp
        val cornerWidth = 4.dp

        // Sol üst köşe
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sağ üst köşe
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sol alt köşe
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sağ alt köşe
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(2.dp)
        ) {}
    }
}

private fun bindCamera(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    onQRCodeDetected: (String, String?, String, String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        // Preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // Image Analysis
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    QRCodeAnalyzer(context, onQRCodeDetected, onError)
                )
            }

        // Kamera seçici
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // Mevcut bağlantıları kaldır
        cameraProvider.unbindAll()

        // Kamera bağlantısını başlat
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )

    } catch (exc: Exception) {
        Log.e("CameraX", "Kamera bağlantısı başarısız", exc)
        onError("Kamera başlatılamadı: ${exc.message}")
    }
}

class QRCodeAnalyzer(
    private val context: Context,
    private val onQRCodeDetected: (String, String?, String, String) -> Unit,
    private val onError: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        when (barcode.valueType) {
                            Barcode.TYPE_TEXT,
                            Barcode.TYPE_URL,
                            Barcode.TYPE_CONTACT_INFO,
                            Barcode.TYPE_EMAIL,
                            Barcode.TYPE_PHONE,
                            Barcode.TYPE_SMS,
                            Barcode.TYPE_WIFI,
                            Barcode.TYPE_GEO -> {
                                barcode.displayValue?.let { value ->
                                    // QR kod bulunduğunda görüntüyü kaydet
                                    val imagePath = saveImageToFile(imageProxy, context)
                                    // QR kod türünü belirle
                                    val codeType = getQRCodeType(barcode.valueType)
                                    // Tarih ve saati al
                                    val dateTime = getCurrentDateTime()
                                    onQRCodeDetected(value, imagePath, codeType, dateTime)
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("QRCodeAnalyzer", "Barkod tarama hatası", exception)
                    onError("Tarama hatası: ${exception.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun saveImageToFile(imageProxy: ImageProxy, context: Context): String? {
        return try {
            val bitmap = imageProxyToBitmap(imageProxy)
            val file = File(context.filesDir, "qr_image_${System.currentTimeMillis()}.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("QRCodeAnalyzer", "Resim kaydetme hatası", e)
            null
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 85, out)
        val imageBytes = out.toByteArray()
        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // Görüntü rotasyonunu düzelt
        val matrix = Matrix()
        matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        return bitmap
    }

    private fun getQRCodeType(valueType: Int): String {
        return when (valueType) {
            Barcode.TYPE_TEXT -> "TEXT"
            Barcode.TYPE_URL -> "URL"
            Barcode.TYPE_CONTACT_INFO -> "CONTACT_INFO"
            Barcode.TYPE_EMAIL -> "EMAIL"
            Barcode.TYPE_PHONE -> "PHONE"
            Barcode.TYPE_SMS -> "SMS"
            Barcode.TYPE_WIFI -> "WIFI"
            Barcode.TYPE_GEO -> "GEO"
            else -> "UNKNOWN"
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}