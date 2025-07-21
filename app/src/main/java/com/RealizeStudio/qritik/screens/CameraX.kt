// CameraQRScanner.kt - Düzeltilmiş versiyon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.viewModel.CameraViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
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
    navController: NavController,
    onCameraReady: (Camera) -> Unit,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    cameraViewModel: CameraViewModel = viewModel(),
    imageUri: Uri?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var detectedCode by remember { mutableStateOf<String?>(null) }
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    var qrCodeType by remember { mutableStateOf<String?>(null) }
    var scanDateTime by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var cameraInitialized by remember { mutableStateOf(false) }


    // lensFacing değiştiğinde state'leri sıfırla
    LaunchedEffect(lensFacing) {
        detectedCode = null
        capturedImagePath = null
        qrCodeType = null
        scanDateTime = null
        isProcessing = false
        cameraInitialized = false
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(16.dp))) {

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                previewView
            },
            update = { previewView ->
                val executor = ContextCompat.getMainExecutor(context)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val camera = cameraViewModel.bindCamera(
                            cameraProvider = cameraProvider,
                            previewView = previewView,
                            lifecycleOwner = lifecycleOwner,
                            context = context, // Bu satır eksikti
                            lensFacing = lensFacing,
                            onQRCodeDetected = { code, imagePath, codeType, dateTime ->
                                if (!isProcessing) {
                                    Log.d("QRScanner", "QR kod bulundu: $code")
                                    isProcessing = true
                                    detectedCode = code
                                    capturedImagePath = imagePath
                                    qrCodeType = codeType
                                    scanDateTime = dateTime
                                    onQRCodeDetected(code)
                                }
                            },
                            onError = { error ->
                                Log.e("QRScanner", "Hata: $error")
                                isProcessing = false
                                onError(error)
                            }
                        )
                        onCameraReady(camera)
                        cameraInitialized = true
                        Log.d("QRScanner", "Kamera başarıyla başlatıldı")
                    } catch (e: Exception) {
                        Log.e("CameraQRScanner", "Kamera başlatma hatası", e)
                        onError("Kamera başlatılamadı: ${e.message}")
                    }
                }, executor)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Kamera başlatılmadıysa loading göster
        if (!cameraInitialized) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White
                )
            }
        } else {
            ScannerOverlay(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // İşlenme durumu göstergesi
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier.wrapContentSize(),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QR kod işleniyor...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // QR kod bulunduğunda navigasyon
        detectedCode?.let { code ->
            LaunchedEffect(code) {
                Log.d("QRScanner", "Navigasyon başlatılıyor: $code")
                try {
                    // Daha uzun gecikme
                    delay(500)

                    // URL encode işlemi
                    val encodedCode = Uri.encode(code)
                    val encodedImagePath = Uri.encode(capturedImagePath ?: "")
                    val encodedCodeType = Uri.encode(qrCodeType ?: "BILINMEYEN")
                    val encodedDateTime = Uri.encode(scanDateTime ?: "")

                    val route = "ScannerResult/$encodedCode/$encodedImagePath/$encodedCodeType/$encodedDateTime"
                    Log.d("QRScanner", "Navigasyon rotası: $route")

                    navController.navigate(route) {
                        // Geri tuşuyla bu ekrana dönmeyi engelle
                        popUpTo("CameraScreen") {
                            inclusive = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CameraQRScanner", "Navigasyon hatası", e)
                    isProcessing = false
                    // State'i sıfırla
                    detectedCode = null
                    onError("Navigasyon hatası: ${e.message}")
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(250.dp)
    ) {
        // Köşe çizgileri
        val cornerLength = 40.dp
        val cornerWidth = 4.dp
        val cornerColor = Color.White

        // Sol üst köşe
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sağ üst köşe
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sol alt köşe
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        // Sağ alt köşe
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerLength, cornerWidth),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}

        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerWidth, cornerLength),
            colors = CardDefaults.cardColors(containerColor = cornerColor),
            shape = RoundedCornerShape(2.dp)
        ) {}
    }
}



class QRCodeAnalyzer(
    private val context: Context,
    private val onQRCodeDetected: (String, String?, String, String) -> Unit,
    private val onError: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var lastDetectionTime = 0L
    private val detectionCooldown = 1500L // 1.5 saniye cooldown
    private var isProcessing = false

    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val currentTime = System.currentTimeMillis()

            // Çok sık tarama yapılmasını önle
            if (currentTime - lastDetectionTime < detectionCooldown) {
                imageProxy.close()
                return
            }

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (!isProcessing && barcodes.isNotEmpty()) {
                        run loop@ {
                            for (barcode in barcodes) {
                                barcode.displayValue?.let { value ->
                                    if (value.isNotBlank() && !isProcessing) {
                                        isProcessing = true
                                        lastDetectionTime = currentTime

                                        Log.d("QRCodeAnalyzer", "QR kod bulundu: $value")

                                        // QR kod bulunduğunda görüntüyü kaydet
                                        val imagePath = saveImageToFile(imageProxy, context)
                                        // QR kod türünü belirle
                                        val codeType = getQRCodeType(barcode.valueType)
                                        // Tarih ve saati al
                                        val dateTime = getCurrentDateTime()

                                        onQRCodeDetected(value, imagePath, codeType, dateTime)
                                        return@loop
                                    }
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
            val imagesDir = File(context.filesDir, "qr_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            val file = File(imagesDir, "qr_image_${System.currentTimeMillis()}.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
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
            Barcode.TYPE_TEXT -> "METIN"
            Barcode.TYPE_URL -> "URL"
            Barcode.TYPE_CONTACT_INFO -> "KONT_BLG"
            Barcode.TYPE_EMAIL -> "E_POSTA"
            Barcode.TYPE_PHONE -> "TELEFON"
            Barcode.TYPE_SMS -> "SMS"
            Barcode.TYPE_WIFI -> "WIFI"
            Barcode.TYPE_GEO -> "KONUM"
            else -> "BILINMEYEN"
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("tr", "TR"))
        return dateFormat.format(Date())
    }
}